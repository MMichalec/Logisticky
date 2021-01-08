package com.example.logisticky

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.example.logisticky.viewLayer.ProductsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import java.lang.Exception


//TODO for future update - add checkboxes to delivery list view with selectable status for delivery


//TODO - Token validation should occur on every fragment
//TODO - fix views on multiple screens.
//TODO - on register fragment talk with Motyl about type of error you want to display
//TODO - get warehouse name for delivery info product
//TODO - add amount in unit to DeliveryInfoFragment. Currently shows only in packages. If dispatch would return also product ID I could grab that data from other endpoint

//TODO ROADMAP : Regiester messages change to proper messages, secure socketTimeout and NoNetwork, Display delivery status on deliveryInfoFragment. If status is cancelled do not allow to cancel anymore, Hide keyboard after inputing credentials




class MainActivity : AppCompatActivity() {
    var isTokenValid:Boolean = false

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

        val token = TokenManager.loadData(this)

        try {
            isTokenValid =  runBlocking {   TokenManager.isTokenValid(token!!)}
        }catch (e: Exception){
            println("Debug : No network")
        }

        if(!isTokenValid) relogin()
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

    private fun relogin (){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unauthorized access")
        builder.setMessage("Token has expired, pleas log in again.")
        builder.setPositiveButton("LOGIN") { dialogInterface: DialogInterface, i: Int ->

            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
        builder.show()
    }

}