package com.example.logisticky.viewLayer

import android.content.Intent
import android.os.Build
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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

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

    var communicationToServerStatus = 0

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



                fetchJson(id,pw)


                //Get rid of this shit!!!
                while (communicationToServerStatus == 0) { Thread.sleep(500)}

                Toast.makeText(activity, communicationToServerStatus.toString(), Toast.LENGTH_LONG).show()


                if (communicationToServerStatus == 200) {

                    MainActivity.globalVar = true
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent);
                    activity?.finish()
                } else {
                    //Toast.makeText(activity, "Invalid ID or/and PW", Toast.LENGTH_LONG).show()
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

    fun fetchJson(id:String, pw:String){
        println("Attempting to Fetch JSON")

        val url = "https://dystproapi.azurewebsites.net/auth/login"

        val client = OkHttpClient()

        val JSON = MediaType.parse("application/json;charset=utf-8")

        val loginData = JSONObject()
        loginData.put("email", id)
        loginData.put("password", pw)


        val body: RequestBody = RequestBody.create(JSON, loginData.toString())
        val newRequest = Request.Builder().url(url).post(body).build()

        client.newCall(newRequest).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                println("Data access succesful")



                val body = response.body()?.string()
                println(body)
                val httpCode = response.code()
                println(httpCode)


                communicationToServerStatus = httpCode
//                val gson = GsonBuilder().create()
//                val listFromJson = gson.fromJson(body, ProductsFragment.ProductList::class.java)
//
//                testList.addAll(listFromJson.products)
//                isPreloaderVisible=false
              // refreshFragment()

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data not loaded")
                communicationToServerStatus = 503
            }
        })

    }

    fun refreshFragment(){
        val ft = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

}