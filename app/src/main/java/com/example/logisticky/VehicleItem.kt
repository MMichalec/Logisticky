package com.example.logisticky

import android.widget.CheckBox

data class VehicleItem(val plateNumber:String, val checkBox: CheckBox, var isSelected: Boolean = false)