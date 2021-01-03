package com.example.logisticky

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.example.logisticky.viewLayer.ProductsFragment



//TODO - add dialog popups when critical changes are being made (dialog "Are You Sure" when deleting delivery etc)
//TODO - add scrolls to recyclerView
//TODO - add checkboxes to delivery list view with selectable status fd delivery
//TODO - add preloaders when data is downloaded from DB
//TODO - make check when creating delivery from cartFragment if all checked items are from the same warehouse
//TODO - make sure the date of new delivery is not from the past



class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        println("Debug " + TokenManager.loadData(this))
        if (TokenManager.loadData(this) == null){

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        var isUserLogged = false

    }

}