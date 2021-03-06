package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.example.logisticky.DeliverysHandler
import com.example.logisticky.ProductsHandler
import com.example.logisticky.R
import com.example.logisticky.TokenManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductInfoFragment : Fragment(), View.OnClickListener {
    private lateinit var productId:String

    private var amount = 0
    private var warehouseProductId = 0
    private var packingValue = 1.0

     private lateinit var dataWarehouses: ArrayList<Availability>
     private lateinit var dataJsonProduct: JSONObject
     private lateinit var productInfoBundle: ProductsHandler.ProductInfoFragmentBundle

     private var unitType = "u"
     private val spinnerArray: MutableList<String> = ArrayList()
     private var token:String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productId = requireArguments().getString("productId").toString()
        token = this.activity?.let { TokenManager.loadData(it) }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
                val productInfoFromApi = async {
                    token?.let { ProductsHandler.getDataForProductInfoFragmentFromApi(it,productId.toInt()) }
                }.await()

                if (productInfoFromApi?.responseCode == 200) {

                    productInfoBundle = productInfoFromApi
                    dataWarehouses = productInfoFromApi.warehouses
                    dataJsonProduct = productInfoFromApi.jsonProductObject
                    unitType = dataJsonProduct.getString("unit_name")

                    updateProductInfoFragmentUI()

                }
        }

        view.findViewById<Button>(R.id.addToCartButton).setOnClickListener(this)
    }

    override fun onClick( v: View?) {
        when (v!!.id){
            R.id.addToCartButton -> {
                val anim = AnimationUtils.loadAnimation(this.context, R.anim.scale)

                if (view?.findViewById<EditText>(R.id.productAddPackage)?.text.toString() == "") {
                    amount = 0
                } else {
                    amount =view?.findViewById<EditText>(R.id.productAddPackage)?.text.toString().toInt()
                }

                if (amount == 0) {
                    showInfoDialog("Please specify an amount of product!")
                } else {


                    var amountAvaible =
                        view?.findViewById<TextView>(R.id.productAmountInPackages)?.text.toString()
                            .filter { it.isDigit() }.toInt()

                    if (amount > amountAvaible) {
                        showInfoDialog("There is not enough product in the warehouse. Value has been set to maximum possible amount")
                        amount = amountAvaible
                        view?.findViewById<EditText>(R.id.productAddPackage)
                            ?.setText("$amountAvaible")
                        view?.findViewById<TextView>(R.id.productAddAmount)?.text =
                            (view?.findViewById<TextView>(R.id.productAmountInPackages)?.text.toString()
                                .filter { it.isDigit() }.toInt() * packingValue).toString()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            async {
                                token?.let {
                                    DeliverysHandler.addProductToCart(
                                        it,
                                        warehouseProductId,
                                        amount
                                    )
                                }

                            }.await()
                            view?.findViewById<TextView>(R.id.productAmountInPackages)?.text =
                                (view?.findViewById<TextView>(R.id.productAmountInPackages)?.text.toString()
                                    .filter { it.isDigit() }.toInt() - amount).toString() + " p."
                            view?.findViewById<TextView>(R.id.productAmount)?.text =
                                (view?.findViewById<TextView>(R.id.productAmountInPackages)?.text.toString()
                                    .filter { it.isDigit() }
                                    .toInt() * packingValue).toString() + " $unitType"


                            Snackbar.make(requireView(), "Product added to the cart!", Snackbar.LENGTH_LONG).show()

                            val fab = activity?.findViewById<FloatingActionButton>(R.id.cartFab)
                            fab?.startAnimation(anim)
                            fab?.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.fabNewItem)))


                        }
                    }

                }
            }
        }
    }

    private fun autoRoundingProductAdding (editTextAddAmount: EditText, editTextAddPackage: EditText  ){

        editTextAddAmount?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if (editTextAddAmount.text.isNotEmpty()) {
                        val inputedAmount =
                            editTextAddAmount.text.toString().toDouble()


                        var roundedPackage =
                            (inputedAmount / packingValue).roundToInt()
                        var roundedAmount = roundedPackage * packingValue

                        if (roundedPackage == 0) {
                            roundedPackage = 1
                            roundedAmount = packingValue
                            Toast.makeText(
                                activity,
                                "Value has been rounded",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        if (inputedAmount - roundedAmount > 0.1)
                            Toast.makeText(
                                activity,
                                "Value has been rounded",
                                Toast.LENGTH_LONG
                            ).show()

                        //hide keyboard after input
                        val imm =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                        editTextAddAmount.setText(roundedAmount.toString())
                        editTextAddPackage?.setText(roundedPackage.toString())
                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

        editTextAddPackage?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if (editTextAddPackage.text.isNotEmpty()) {
                        val calculatedPackage = editTextAddPackage.text.toString().toDouble()
                        editTextAddAmount?.setText((calculatedPackage * packingValue).toString())


                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })
    }

    private fun updateProductInfoFragmentUI(){
        activity?.runOnUiThread(object : Runnable {
            override fun run() {

                view?.findViewById<EditText>(R.id.productAddAmount)?.hint = unitType
                view?.findViewById<ProgressBar>(R.id.productInfoLoader)?.visibility = View.GONE

                //Setting up spinners ------------------------------------------------------
                spinnerArray.clear()
                dataWarehouses.forEach{
                    spinnerArray.add(it.warehouseName)
                }

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    view!!.context, R.layout.spinner_item, spinnerArray
                )
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_list)
                val sItems = view!!.findViewById(R.id.magazinePicker) as Spinner
                sItems.adapter = adapter

                sItems.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        p2: Int,
                        p3: Long
                    ) {


                        dataWarehouses.forEach{
                            if (it.warehouseName == spinnerArray[p2])
                            {
                                view!!.findViewById<TextView>(R.id.productAmount).text = (it.amount.toString().toFloat() * dataJsonProduct.getString("unit_number").toFloat()).toString() + " ${dataJsonProduct.getString("unit_name")} "
                                view!!.findViewById<TextView>(R.id.productAmountInPackages).text = "${it.amount.toInt()} p."

                                warehouseProductId = it.product_warehouseId
                            }
                        }

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
                //--------------------------------------------------------------------------



                view?.findViewById<TextView>(R.id.productName)?.text = dataJsonProduct.getString("name")
                packingValue = dataJsonProduct.getString("unit_number").toDouble()
                view?.findViewById<TextView>(R.id.productPrice)?.text = "${dataJsonProduct.getString("price")} zł/p."
                view?.findViewById<TextView>(R.id.productPricePerUnit)?.text = String.format("%.2f", (dataJsonProduct.getString("price").toFloat() / dataJsonProduct.getString("unit_number").toFloat())) + " zł/${dataJsonProduct.getString("unit_name")}"
                view?.findViewById<TextView>(R.id.packing)?.text = "Packing: ${dataJsonProduct.getString("unit_number")} ${dataJsonProduct.getString("unit_name")}"


                //Autocalculating amounts of product
                val editTextAddAmount = view?.findViewById<EditText>(R.id.productAddAmount)
                val editTextAddPackage = view?.findViewById<EditText>(R.id.productAddPackage)

                if (editTextAddPackage != null && editTextAddAmount != null) {
                    autoRoundingProductAdding(editTextAddAmount,editTextAddPackage)
                }
            }
        })
    }
    private fun showInfoDialog(message: String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle("")
        builder.setMessage(message)
        builder.setPositiveButton("Okay") { dialogInterface: DialogInterface, i: Int ->
        }
        builder.show()
    }
    class Availability(var amount: Float, var product_warehouseId: Int, var warehouseName: String)
}