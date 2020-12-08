package com.example.logisticky

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception



open class ProductsAdapter(private var exampleList: List<ProductItem>):
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {




    //Represents a single row in list
    class ProductsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textView1: TextView = itemView.findViewById(R.id.text_view_1)
//        val textView2: TextView = itemView.findViewById(R.id.text_view_2)
//        var checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_list_item,
            parent,
            false
        )

        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.textView1.text = currentItem.name
//        holder.textView2.text = currentItem.text2
//        holder.checkBox = currentItem.checkBox



        holder.itemView.setOnClickListener{
        var gName:String = currentItem.name
        var navController = Navigation.findNavController(it)

            //TODO This condition is bad. I am using exception handling to navigate to proper fragment. I need to implement something like if (previous fragment was this) then (go there)
            if(!TextUtils.isEmpty(currentItem.name)){
                try {
                    val bundle = bundleOf("productId" to currentItem.name)
                    navController!!.navigate(
                        R.id.action_productsFragment_to_productInfoFragment,
                        bundle
                    )
                } catch (e:Exception){
                    val bundle = bundleOf("deliveryId" to currentItem.name)

                    try{
                    navController!!.navigate(R.id.action_deliversFragment_to_deliveryInfoFragment, bundle)
                } catch (e:Exception){}

                }

            }

        }
    }

    override fun getItemCount() = exampleList.size

}