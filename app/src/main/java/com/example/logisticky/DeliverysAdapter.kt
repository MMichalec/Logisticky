package com.example.logisticky

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class DeliverysAdapter(private var exampleList: List<DeliveryItem>):
    RecyclerView.Adapter<DeliverysAdapter.ProductsViewHolder>()  {
    lateinit var context:Context

    //Represents a single row in list
    class ProductsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView1: TextView = itemView.findViewById(R.id.text_view_deliveryId)
        val textView2: TextView = itemView.findViewById(R.id.text_view_deliveryStatus)
        val textView3: TextView = itemView.findViewById(R.id.text_view_creationDate)
        val textView4: TextView = itemView.findViewById(R.id.text_view_pickupDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.deliverys_items_list,
            parent,
            false
        )
        context=parent.context



        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentItem = exampleList.get(position)

        holder.textView1.text = currentItem.deliveryId.toString()
        holder.textView2.text = currentItem.deliveryStatus


        //TODO Add more colurs
        when (currentItem.deliveryStatus){
            "CREATED" -> holder.textView2.setTextColor(ContextCompat.getColor(context, R.color.accept))
            "CANCELED" -> holder.textView2.setTextColor(ContextCompat.getColor(context, R.color.cancel))
            "WAITING" -> holder.textView2.setTextColor(ContextCompat.getColor(context, R.color.waiting))
            "REALIZED" -> holder.textView2.setTextColor(ContextCompat.getColor(context, R.color.hint))
        }


        holder.textView3.text = currentItem.date
        holder.textView4.text = currentItem.dueDate


        holder.itemView.setOnClickListener{

            val navController = Navigation.findNavController(it)
            val bundle = bundleOf("deliveryId" to currentItem.deliveryId.toString())
            navController.navigate(R.id.action_deliversFragment_to_deliveryInfoFragment, bundle)
        }


    }

    override fun getItemCount() = exampleList.size

}