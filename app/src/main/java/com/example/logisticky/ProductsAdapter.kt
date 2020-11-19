package com.example.logisticky

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.viewLayer.ProductsFragment

open class ProductsAdapter(private var exampleList: List<ProductItem>):
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {




    //Represents a single row in list
    class ProductsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textView1: TextView = itemView.findViewById(R.id.text_view_1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false)

        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.textView1.text = currentItem.text1

        holder.itemView.setOnClickListener{
        var gName:String = currentItem.text1
        var navController = Navigation.findNavController(it)



            if(!TextUtils.isEmpty(currentItem.text1)){
                val bundle = bundleOf("productId" to currentItem.text1)
                navController!!.navigate(R.id.action_productsFragment_to_productInfoFragment,bundle)
            }


        }
    }

    override fun getItemCount() = exampleList.size


}