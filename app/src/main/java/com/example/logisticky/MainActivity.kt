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
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.logisticky.viewLayer.ProductsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.IllegalArgumentException


//TODO for future update - add checkboxes to delivery list view with selectable status for delivery

class MainActivity : AppCompatActivity() {
    var isTokenValid:Boolean = false
    var isConnection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val networkConnection = NetworkConnection(applicationContext)

            networkConnection.observe(this, Observer {isConnected ->
                isConnection = isConnected
                if(isConnected){
                    println("Debug: CONNECTION - IS CONNECTED!")
                    if (TokenManager.loadData(this) == null) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent);
                        finish()
                    }

                }else {
                    println("Debug: CONNECTION - IS DISCONNECTED!")
                    noNetworkPopup()
                }
            })


        val token = TokenManager.loadData(this)

        try {
            isTokenValid =  runBlocking {   TokenManager.isTokenValid(token!!)}
        }catch (e: Exception){
            println("Debug : No network")
        }

        if(!isTokenValid)
        {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            this.finish()
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
    private fun showInfoDialog(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("WARNING")
        builder.setMessage(message)
        builder.setOnDismissListener(DialogInterface.OnDismissListener { dialog ->
            if(!isConnection)
                noNetworkPopup()
        })

        builder.setPositiveButton("Try again") { dialogInterface: DialogInterface, i: Int ->
            if(!isConnection)
                noNetworkPopup()
        }
        builder.show()
    }

    private fun noNetworkPopup(){
        this.runOnUiThread(object : Runnable {
            override fun run() {
                showInfoDialog("There seems to be a problem with network connectivity! Check your connection.")
            }
        })
    }


}