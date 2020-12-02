package com.example.logisticky

import android.widget.CheckBox

data class DriverItem(val name:String, val surname:String, val checkBox: CheckBox, var isSelected: Boolean = false)