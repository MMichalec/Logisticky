package com.example.logisticky

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity

class TokenManager {

    companion object {

        fun saveData(activity: FragmentActivity, data: String?){
            val sharedPreferences: SharedPreferences = activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.apply(){
                putString("STRING_KEY", data)
            }.apply()
        }

        fun loadData(activity: FragmentActivity):String? {
            val sharedPreferences: SharedPreferences? = activity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

            val savedString = sharedPreferences?.getString("STRING_KEY",null)

            return savedString
        }
    }
}

