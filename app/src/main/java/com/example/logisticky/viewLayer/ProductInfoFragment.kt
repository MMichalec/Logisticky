package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
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
    lateinit var productId:String

    var amount = 0
    var warehouseProductId = 0

    lateinit var dataWarehouses: ArrayList<Availability>
    lateinit var dataJsonProduct: JSONObject
    lateinit var testData: ProductsHandler.ProductInfoFragmentBundle

    var unitType = "u"

    val spinnerArray: MutableList<String> = ArrayList()
    var packingValue = 1.0
    var token:String? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        productId = requireArguments().getString("productId").toString()
        token = this.activity?.let { TokenManager.loadData(it) }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_product_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {


                val dataFromAPI = async {
                    token?.let { ProductsHandler.getDataForProductInfoFragmentFromApi(it,productId.toInt()) }
                }.await()

                if (dataFromAPI?.responseCode == 200) {

                    testData = dataFromAPI
                    dataWarehouses = dataFromAPI.warehouses
                    dataJsonProduct = dataFromAPI.jsonProductObject
                    unitType = dataJsonProduct.getString("unit_name")

                    updateProductInfoFragmentUI(dataWarehouses, dataJsonProduct)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    class Availability(var amount: Float, var product_warehouseId: Int, var warehouseName: String)

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
                        val calculatedPackage =
                            editTextAddPackage.text.toString().toDouble()
                        editTextAddAmount?.setText((calculatedPackage * packingValue).toString())


                        //hide keyboard after input
                        val imm =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

    }

    private fun updateProductInfoFragmentUI(warehouseList:ArrayList<Availability>, json_Product:JSONObject){
        activity?.runOnUiThread(object : Runnable {
            override fun run() {




                view?.findViewById<EditText>(R.id.productAddAmount)?.hint = unitType
                view?.findViewById<ProgressBar>(R.id.productInfoLoader)?.visibility = View.GONE
                //Setting up spinners ------------------------------------------------------
                spinnerArray.clear()
                warehouseList.forEach{
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


                        warehouseList.forEach{
                            if (it.warehouseName == spinnerArray[p2])
                            {
                                view!!.findViewById<TextView>(R.id.productAmount).text = (it.amount.toString().toFloat() * json_Product.getString("unit_number").toFloat()).toString() + " ${json_Product.getString("unit_name")} "
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



                view?.findViewById<TextView>(R.id.productName)?.text =
                    json_Product.getString(
                        "name"
                    )
                packingValue = json_Product.getString("unit_number").toDouble()
                view?.findViewById<TextView>(R.id.productPrice)?.text = "${json_Product.getString("price")} zł/p."
                view?.findViewById<TextView>(R.id.productPricePerUnit)?.text = String.format("%.2f", (json_Product.getString("price").toFloat() / json_Product.getString("unit_number").toFloat())) + " zł/${json_Product.getString("unit_name")}"
                view?.findViewById<TextView>(R.id.packing)?.text = "Packing: ${json_Product.getString("unit_number")} ${json_Product.getString("unit_name")}"


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

}