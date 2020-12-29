package com.example.logisticky

import android.widget.CheckBox

class CartItem(val productName:String, val magazineName:String, val packageAmount:String, val reservationId:Int, val checkBox: CheckBox, var isVisible:Boolean = true, var isSelected: Boolean = false) {

}