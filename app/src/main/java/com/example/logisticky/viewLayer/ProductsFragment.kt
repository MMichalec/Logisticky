package com.example.logisticky.viewLayer

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView

    var productsListForRecyclerView = ArrayList<ProductItem>()
    val displayList = ArrayList<ProductItem>()

    var token:String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsListForRecyclerView.clear()

        CoroutineScope(Dispatchers.IO).launch {

                val getProductsListApiResponseCode = async {
                    token?.let { ProductsHandler.getDataForProductsFragmentFromApi(it,productsListForRecyclerView) }
                }.await()

                if (getProductsListApiResponseCode == 200) {
                    updateProductsFragmentUI()
                }


        }
        recyclerView = view.findViewById(R.id.products_recycleView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem = menu.findItem(R.id.search)

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

                        displayList.addAll(productsListForRecyclerView.sortedBy { it.name })

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


    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.visibility = View.VISIBLE
    }

    private fun updateProductsFragmentUI(){
        activity?.runOnUiThread(object : Runnable {
            override fun run() {
                activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.visibility = View.GONE
                displayList.clear()
                displayList.addAll(productsListForRecyclerView.sortedBy { it.name })

                recyclerView.adapter = ProductsAdapter(displayList)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                view?.findViewById<ProgressBar>(R.id.productsLoader)?.visibility = View.GONE
            }
        })
    }


    class ProductList(var products: ArrayList<SimpleProduct>)
    class SimpleProduct(var name:String, var product_id: Int)

}