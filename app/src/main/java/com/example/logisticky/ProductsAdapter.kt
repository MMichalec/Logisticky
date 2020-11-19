package com.example.logisticky

import android.util.Log.d
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

open class ProductsAdapter(private var exampleList: List<ProductItem>, private var exampleListFull: List<ProductItem> = ArrayList<ProductItem>(exampleList)):
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
    }

    override fun getItemCount() = exampleList.size


}