package com.example.logisticky

import com.example.logisticky.viewLayer.ProductInfoFragment
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.math.log

class DriversHandler {

    data class Driver (val driver_id:Int, val name:String, val surname:String)


    companion object{

        fun getDataForSettingsDriversFragmentFromApi(token:String):ArrayList<Driver>{
            println("Debug : Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/drivers"

            val client = OkHttpClient()

            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()

            println("Debug: Data access succesful URL: $url")

            val body = response.body()?.string()
            println("Debug: Pobietanie kierwocow $body")
            println("Debug: ${response.code()}")

            val json = JSONObject(body)
            var driversList = ArrayList<Driver>()
            var jsonArray_Drivers = json.getJSONArray("drivers")

            for (i in 0 until jsonArray_Drivers.length()) {

                val driverObject = jsonArray_Drivers.getJSONObject(i)
                driversList.add(
                    Driver(
                        driverObject.getString("driver_id").toInt(),
                        driverObject.getString("name"),
                        driverObject.getString("surname")
                    )
                )
            }

            return driversList
        }

        fun addDriver(token:String, name:String, surname:String):Int{

            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/drivers"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")

            val driverObject = JSONObject()

            val driverDataObject = JSONObject()


            driverDataObject.put("name", name)
            driverDataObject.put("surname",surname)

            driverObject.put("driver", driverDataObject)


            println("Debug: ${driverObject}")


            val body: RequestBody = RequestBody.create(JSON, driverObject.toString())
            val newRequest = Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()
            //communicationToServerStatus= response.code()

            return response.code()
        }


        fun deleteDriver(token: String, id:Int): Int{
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/drivers/$id"

            val client = OkHttpClient()


            val newRequest = Request.Builder().header("x-access-token", token).url(url).delete().build()

            val response = client.newCall(newRequest).execute()
            //communicationToServerStatus= response.code()
            println("Debug: ${response.code()}")

            return response.code()
        }

    }




}