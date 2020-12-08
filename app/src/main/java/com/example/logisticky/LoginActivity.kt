package com.example.logisticky

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        findViewById<Button>(R.id.logInButton).setOnClickListener (this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.logInButton -> {
                val id = findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
                val pw = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

                if (id == "test" && pw == "test"){

                    MainActivity.globalVar = true
                    val intent = Intent(this, MainActivity::class.java )
                    startActivity(intent);
                    finish()
                } else {
                    Toast.makeText(this, "Invalid ID or/and PW", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

}

