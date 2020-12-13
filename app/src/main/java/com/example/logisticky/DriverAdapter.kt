package com.example.logisticky

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.sql.Driver

class DriverAdapter (private var exampleList:List<DriverItem>):
    RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {
    private val items = ArrayList<DriverItem>()


    class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_view_list1)
        val surname: TextView = itemView.findViewById(R.id.text_view_list2)
        var checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_list_item2,
            parent,
            false
        )
        return DriverViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val currentItem = exampleList.get(position)

        holder.name.text = currentItem.name
        holder.surname.text = currentItem.surname

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = currentItem.isSelected

        holder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                currentItem.isSelected= true
            }
        })

    }

    override fun getItemCount() = exampleList.size

}


