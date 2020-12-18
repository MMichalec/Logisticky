package com.example.logisticky.viewLayer

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
    var testList = ArrayList<ProductItem2>()
    var displayList = ArrayList<ProductItem2>()
    val itemsToRemoveList = ArrayList<ProductItem2>()
    lateinit var recyclerView: RecyclerView
    var token:String? = null
    lateinit var navController: NavController

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
        testList = generateDummyList(1) as ArrayList<ProductItem2>
        token = this.activity?.let { TokenManager.loadData(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        CoroutineScope(Dispatchers.IO).launch{

            var dataFromAPI = async {
                testList.clear()
                token?.let { DeliverysHandler.getCartList(it) }
                //token?.let { DeliverysHandler.addProductToCart(it,1,100) }

            }.await()

            println(dataFromAPI?.responseCode)

            dataFromAPI?.cartProductsItemList?.forEach{
                println("Debug: Product ${it.productName}, amount: ${it.amount},  ${it.warehouseName}")
                val checkBox = CheckBox(activity)
                testList.add(ProductItem2(it.productName, it.amount.toString(),checkBox))
                totalPrice += it.price
            }
            updateUI()
        }




        view.findViewById<Button>(R.id.cartRemoveSelectedButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.cartAddProductButton).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cartRemoveSelectedButton -> {
                displayList.forEach {  if (it.isSelected) itemsToRemoveList.add(it) }
                itemsToRemoveList.forEach{ displayList.remove(it)}
                //TODO add removing items from database here
            }

            R.id.cartAddProductButton -> navController.navigate(R.id.action_cartFragment_to_productsFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem = menu!!.findItem(R.id.searchProducts)

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

                            if (it.text1.toLowerCase(Locale.getDefault()).contains(search)) {
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

    private fun generateDummyList(size: Int): List<ProductItem2>{
        val list = ArrayList<ProductItem2>()

        val checkBox = CheckBox(activity)
        for (i in 0 until size){
            val item = ProductItem2("Item $i","Amount $i", checkBox)
            list +=item
        }
        return list
    }

    fun refreshFragment(){
        val ft = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

    private fun updateUI(){

        activity?.runOnUiThread(object: Runnable {
            override fun run() {
                displayList = testList

                recyclerView = view?.findViewById(R.id.cart_recycleView)!!
                recyclerView?.adapter = ProductsAdapter2(displayList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)

                view!!.findViewById<TextView>(R.id.cartPriceText).text = String.format("%.2f", totalPrice )
            }

        })


    }
}