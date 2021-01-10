package com.example.logisticky

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer

class LoginActivity : AppCompatActivity() {
    var isConnection = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //setSupportActionBar(findViewById(R.id.toolbar2))
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val networkConnection = NetworkConnection(applicationContext)

        networkConnection.observe(this, Observer { isConnected ->
            isConnection = isConnected
            if (isConnected) {
                println("Debug: CONNECTION - IS CONNECTED!")

            } else {
                println("Debug: CONNECTION - IS DISCONNECTED!")
                noNetworkPopup()
            }
        })

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

