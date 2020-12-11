package com.example.logisticky.viewLayer

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment() {
    var productsListForRecyclerView = ArrayList<ProductItem>()
    val displayList = ArrayList<ProductItem>()
    lateinit var pAdapter: ProductsAdapter

    var token:String? = null
    var isPreloaderVisible = true;


    lateinit var recyclerView: RecyclerView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)
        token = this.activity?.let { TokenManager.loadData(it) }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsListForRecyclerView.clear()
        fetchJson()
        recyclerView = view?.findViewById(R.id.products_recycleView)

        println("Debug: Succesfuly shared token: $token")

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
                        productsListForRecyclerView.forEach {

                            if (it.name.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                        }


                        recyclerView.adapter!!.notifyDataSetChanged()
                    } else {
                        displayList.clear()
                        displayList.addAll(productsListForRecyclerView)
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }

            })

        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun fetchJson(){
        println("Debug : Attempting to Fetch JSON")

        val url = "https://dystproapi.azurewebsites.net/products"

        val client = OkHttpClient()


        val request = Request.Builder().header("x-access-token", token).url(url).build()
        println("Debug: token in fetchJson $token")
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                println("Debug: Data access succesful")

                val body = response.body()?.string()
                println("Debug: $body")

                val gson = GsonBuilder().create()
                val simpleProducts = gson.fromJson(body, ProductList::class.java)

                simpleProducts.products.forEach{

                    productsListForRecyclerView.add(ProductItem(it.name))

                }

                isPreloaderVisible=false

                activity?.runOnUiThread(object : Runnable {
                    override fun run() {
                        displayList.clear()
                        displayList.addAll(productsListForRecyclerView)

                        pAdapter = ProductsAdapter(displayList)
                        recyclerView?.adapter = pAdapter
                        recyclerView?.layoutManager = LinearLayoutManager(activity)
                        recyclerView?.setHasFixedSize(true)
                        view?.findViewById<ProgressBar>(R.id.productsLoader)?.visibility = View.GONE
                    }
                })


            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data not loaded")
                productsListForRecyclerView.add(ProductItem("COULD NOT LOAD DATA. CHECK YOUR NETWORK CONNECTION"))
            }
        })
    }

    private fun findIdByProductName (name:String, list:ArrayList<SimpleProduct>): Int? {
        list.forEach{
            if (it.name == name) {
                return it.product_id
            }
        }
        return null
    }


    class ProductList(var products: ArrayList<SimpleProduct>)

    class SimpleProduct(var name:String, var product_id: Int)

}