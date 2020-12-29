package com.example.logisticky

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapter3(private var exampleList: List<CartItem>):
    RecyclerView.Adapter<ProductsAdapter3.ProductsViewHolder3>()  {

    //Represents a single row in list
    class ProductsViewHolder3(itemView: View):RecyclerView.ViewHolder(itemView){
        val textView1: TextView = itemView.findViewById(R.id.text_view_list1)
        val textView2: TextView = itemView.findViewById(R.id.text_view_list2)
        val textView3: TextView = itemView.findViewById(R.id.text_view_list3)
        var checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder3 {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cart_items_list,
            parent,
            false
        )



        return ProductsViewHolder3(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder3, position: Int) {
        val currentItem = exampleList.get(position)

        holder.textView1.text = currentItem.productName
        holder.textView2.text = currentItem.magazineName
        holder.textView3.text = currentItem.packageAmount


        if(!currentItem.isVisible){
            holder.checkBox.visibility = View.GONE
        }





        //Making recyclerView remember checkboxes without multiplicating them
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isSelected

        holder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Toast.makeText(holder.itemView.context, currentItem.productName, Toast.LENGTH_LONG).show()
                currentItem.isSelected= true
            }
        })







        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, "Value has been rounded", Toast.LENGTH_LONG).show()
        }


    }

    override fun getItemCount() = exampleList.size

}


