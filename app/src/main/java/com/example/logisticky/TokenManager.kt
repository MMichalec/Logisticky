package com.example.logisticky

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonParser
import kotlinx.coroutines.*

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class TokenManager {

    class AuthMe(val responseCode: Int, val roles: ArrayList<String>?)

    companion object {


        //TODO I think save data should be privet LoginFragment function not public avaible from handler
        fun saveData(activity: FragmentActivity, data: String?) {
            val sharedPreferences: SharedPreferences = activity.getSharedPreferences(
                "sharedPrefs",
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()

            editor.apply() {
                putString("STRING_KEY", data)
            }.apply()
        }

        fun loadData(activity: FragmentActivity): String? {
            val sharedPreferences: SharedPreferences? = activity.getSharedPreferences(
                "sharedPrefs",
                Context.MODE_PRIVATE
            )

            val savedString = sharedPreferences?.getString("STRING_KEY", null)

            return savedString
        }

        fun register(id: String, pw: String): String {
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/auth/register"

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
            println("Debug: ${response.message()}")



            val jsonBody = response.body()?.string()


            val msg = JSONObject(jsonBody)
val msg2 = msg.getJSONObject("error").getString("message")
            println("Debug auth token from msg: $msg2")

            println("Debug auth token from body: $jsonBody}")

            return msg2
        }

        fun getPermissions(token: String): AuthMe {
            var responseCode = 0
            var roles = ArrayList<String>()


            val url = "https://dystproapi.azurewebsites.net/auth/me/roles"
            val client = OkHttpClient()


            val request = Request.Builder().header("x-access-token", token).url(url).build()

            val response = client.newCall(request).execute()
            responseCode = response.code()


            println("Debug: auth/me route access $responseCode")

            val body = response.body()?.string()
            val json = JSONObject(body)

            println("Debug: permissions and roles for current account$body")


            val jsonArray = json.getJSONArray("roles")
            println("Debug: permissions and roles for current account $jsonArray")
            for (i in 0 until jsonArray.length()) {
                roles?.add(jsonArray[i].toString())
            }

            println("Debug: permissions and roles for current account ${roles.size}")
            val authMeData = AuthMe(responseCode, roles)
            return authMeData
        }

        fun postRequest(token: String, request: String): Int {
            val url = "https://dystproapi.azurewebsites.net/requests"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")

            val loginData = JSONObject()
            loginData.put("info", request)


            val body: RequestBody = RequestBody.create(JSON, loginData.toString())
            val newRequest =
                Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()

            println("Debug /request route post ${response.code()}")
            println("Debug /request route post ${response.message()}")
            return response.code()
        }

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        }

        fun getDiscount(token: String): Int {
            val url = "https://dystproapi.azurewebsites.net/distributor/discount"

            val client = OkHttpClient()

            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()

            println("Debug: Data access succesful URL: $url")

            val body = response.body()?.string()

            println("Debug: Discount: ${response.code()}")
            val json = JSONObject(body)
            val discount = json.getString("discount").toInt()
            return discount
        }

        suspend fun isTokenValid(token: String): Boolean = withContext(Dispatchers.IO) {

            var isValid = true
            var responseCode = 999

                    val url = "https://dystproapi.azurewebsites.net/auth/me/roles"
                    val client = OkHttpClient()
                    val request = Request.Builder().header("x-access-token", token).url(url).build()
                    val response = client.newCall(request).execute()
                    responseCode = response.code()

                print("Debug: token validation: $responseCode")
                if(responseCode!=200) isValid=false


            return@withContext isValid
        }

    }
}

