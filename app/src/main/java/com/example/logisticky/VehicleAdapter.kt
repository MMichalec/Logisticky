package com.example.logisticky

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter (private  var exampleList:List<VehicleItem>): RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    class VehicleViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView){
        val plateNumber: TextView = itemView.findViewById(R.id.vehicleViewList)
        val checkBox: CheckBox = itemView.findViewById(R.id.vehicleCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.vehicle_list_item,
            parent,
            false
        )
        return VehicleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val currentItem = exampleList.get(position)

        holder.plateNumber.text = currentItem.plateNumber
        holder.checkBox.isChecked = currentItem.isSelected

        holder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Toast.makeText(holder.itemView.context, currentItem.plateNumber, Toast.LENGTH_LONG).show()
                currentItem.isSelected= true
            }
        })
    }

    override fun getItemCount() = exampleList.size
}