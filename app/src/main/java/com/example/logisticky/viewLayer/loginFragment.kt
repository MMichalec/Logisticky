package com.example.logisticky.viewLayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.logisticky.MainActivity
import com.example.logisticky.R
import com.example.logisticky.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.system.measureTimeMillis

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginFragment : Fragment(), View.OnClickListener {
    lateinit var navController: NavController
    lateinit var token: String


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.logInButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.registerButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.logInButton -> {

                val id = view?.findViewById<EditText>(R.id.editTextTextPersonName)?.text.toString()
                val pw = view?.findViewById<EditText>(R.id.editTextTextPassword)?.text.toString()

                CoroutineScope(IO).launch {

                    val executionTime = measureTimeMillis {
                        val communicationToServerStatus = async {

                            activity?.runOnUiThread(object : Runnable {
                                override fun run() {
                                    view?.findViewById<ProgressBar>(R.id.loginLoader)?.visibility =
                                        View.VISIBLE
                                }
                            })

                            fetchJson(id, pw)
                        }.await()


                        if (communicationToServerStatus == 200) {
                            MainActivity.isUserLogged = true
                            val intent = Intent(activity, MainActivity::class.java)

                            startActivity(intent);
                            activity?.finish()
                        } else {
                            Toast.makeText(activity, "Invalid ID or/and PW", Toast.LENGTH_LONG)
                                .show()
                        }
                        activity.let {
                            if (it != null) {
                                TokenManager.saveData(it, token)
                            }
                        }
                    }
                    println("Debug: Login in total elapsed time: $executionTime ms.")
                }



            }

            R.id.registerButton -> navController.navigate(R.id.action_loginFragment_to_registerFragment)

        }
    }


    companion object {


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment loginFragment.
         */
        // TODO: Rename and change types and number of parameters



        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            loginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun fetchJson(id: String, pw: String):Int{
        println("Attempting to Fetch JSON")

        val url = "https://dystproapi.azurewebsites.net/auth/login"

        val client = OkHttpClient()

        val JSON = MediaType.parse("application/json;charset=utf-8")

        val loginData = JSONObject()
        loginData.put("email", id)
        loginData.put("password", pw)


        val body: RequestBody = RequestBody.create(JSON, loginData.toString())
        val newRequest = Request.Builder().url(url).post(body).build()

        val response = client.newCall(newRequest).execute()
        //communicationToServerStatus= response.code()
        println("Debug: ${response.code()}")

        //println("Debug auth body : ${response.body()?.toString()}")

        val jsonBody = response.body()?.string()
        val json = JSONObject(jsonBody)
        token = json.getString("token")


        println("Debug auth token from body: $token}")

        return response.code()
    }



}