package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment(), View.OnClickListener {
    var testList = ArrayList<CartItem>()
    var displayList = ArrayList<CartItem>()
    var cartItemsList = ArrayList<DeliverysHandler.CartProductItem>()
    val itemsToRemoveList = ArrayList<CartItem>()
    lateinit var recyclerView: RecyclerView
    var token:String? = null
    lateinit var navController: NavController
    lateinit var checkBox:CheckBox

    var totalPrice: Float= 0.0F



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //Get list from database here
        checkBox= CheckBox(activity)
        token = this.activity?.let { TokenManager.loadData(it) }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navController.navigate(R.id.action_cartFragment_to_mainMenuFragment)
            clearBackStack()
        }
        this.requireActivity().onBackPressedDispatcher.addCallback(this, callback);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_cart, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        totalPrice = 0.0F
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        CoroutineScope(Dispatchers.IO).launch{

            var dataFromAPI = async {
                testList.clear()
                token?.let { DeliverysHandler.getCartList(it) }

            }.await()



            println(dataFromAPI?.responseCode)

            cartItemsList = dataFromAPI!!.cartProductsItemList
            dataFromAPI?.cartProductsItemList?.forEach{
                println("Debug: Product ${it.productName}, amount: ${it.amount}, ${it.warehouseName}")
                

                var dataFromApi2 = async {
                    getProductInfo(it.productId)
                }.await()

                val amount = dataFromApi2.getString("unit_number").toDouble() * it.amount
                val unit = dataFromApi2.getString("unit_name")


                testList.add(CartItem(it.productName, "W: ${it.warehouseName}", "Amount: ${it.amount} p. | $amount $unit,", it.reservationId, checkBox))
                totalPrice += it.price
            }
            updateUI()
        }




        view.findViewById<Button>(R.id.cartRemoveSelectedButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.cartAddProductButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.cartMakeDeliveryButton).setOnClickListener(this)

    }

    override fun onClick(v: View?) {


        when (v!!.id) {


            R.id.cartRemoveSelectedButton -> {
                itemsToRemoveList.clear()
                displayList.forEach { if (it.isSelected) itemsToRemoveList.add(it) }
                itemsToRemoveList.forEach { displayList.remove(it) }
                //TODO add removing items from database here

//                val testList = ArrayList<Int>()
//                val testList2 =ArrayList<Int>()
//
//                itemsToRemoveList.forEach{
//                    testList.add(it.reservationId)
//                }
//
//                cartItemsList.forEach{
//                    testList2.add(it.reservationId)
//                }
//
//                testList.intersect(testList2).forEach{id ->
//                    println("Debug: reservationsIdToDelete $id")
//                }

                //its ugly. You can do this with the use of intersect as above
                cartItemsList.forEach {
                    itemsToRemoveList.forEach() {itRemoveList ->
                        if (it.reservationId == itRemoveList.reservationId) {
                            println("Debug: Cart Item deleted code: ${it.productName}")
                            totalPrice -= it.price
                            var idToDelete = it.reservationId
                            CoroutineScope(Dispatchers.IO).launch {

                                val dataFromApi2 = async {
                                    token?.let {
                                        DeliverysHandler.removeProductFromCart(
                                            it,
                                            idToDelete
                                        )
                                    }
                                }.await()
                                println("Debug: Cart Item deleted code: ${it.productName}")
                            }
                        }
                    }
                }
                updateUI()
            }

            R.id.cartAddProductButton -> navController.navigate(R.id.action_cartFragment_to_productsFragment)

            R.id.cartMakeDeliveryButton -> {


                var warehousesList = ArrayList<String>()
                var reservationList = ArrayList<Int>()

                displayList.forEach {
                    if (it.isSelected) {
                        reservationList.add(it.reservationId)
                        warehousesList.add(it.magazineName)
                    }
                }
                if (reservationList.isEmpty()) {
                    showInfoDialog("No products selected. Please select at least one product.")
                } else if (warehousesList.distinct().size > 1){
                    showInfoDialog("Selected products are from different warehouses. Please select products from the same warehouse.")
                }
                else {

                    val warehouseName = warehousesList[0]

                    val bundle = bundleOf("deliveryId" to warehouseName)
                    bundle.putIntegerArrayList("reservationsIdList", reservationList)
                    navController.navigate(R.id.action_cartFragment_to_makeDeliveryFragment, bundle)
                }
                displayList.forEach{
                    it.isSelected = false
                }
                updateUI()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem = menu!!.findItem(R.id.search)

        if(menuItem != null){

            val searchView = menuItem.actionView as SearchView
            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search Your Product..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        testList.forEach {

                            if (it.productName.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                        }


                        recyclerView.adapter!!.notifyDataSetChanged()
                    } else {

                        displayList.clear()
                        displayList.addAll(testList)

                        recyclerView.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }

            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    private fun updateUI(){

        activity?.runOnUiThread(object: Runnable {
            override fun run() {
                activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.setBackgroundTintList(ColorStateList.valueOf(resources.getColor( R.color.fab)))
                view?.findViewById<ProgressBar>(R.id.cartLoader)?.visibility = View.GONE
                displayList = testList



                recyclerView = view?.findViewById(R.id.cart_recycleView)!!
                recyclerView?.adapter = ProductsAdapter3(displayList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)


                view!!.findViewById<TextView>(R.id.cartPriceText).text = "${String.format("%.2f", totalPrice )} PLN"
            }

        })


    }

    private fun getProductInfo(productId:Int): JSONObject {
        lateinit var dataJsonProduct:JSONObject
            val dataFromAPI = token?.let { ProductsHandler.getDataForProductInfoFragmentFromApi(it,productId) }
                dataJsonProduct = dataFromAPI!!.jsonProductObject
            return dataJsonProduct
        }

    fun showInfoDialog(message:String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle ("")
        builder.setMessage (message)
        builder.setPositiveButton("Okay",{ dialogInterface: DialogInterface, i: Int -> } )
        builder.show()
    }
    private fun clearBackStack() {
        val manager: FragmentManager? = getFragmentManager()
        if (manager != null) {
            if (manager.backStackEntryCount > 0) {
                val first = manager.getBackStackEntryAt(0)
                manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    }
