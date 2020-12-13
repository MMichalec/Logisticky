package com.example.logisticky

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class VehicleHandler {

    data class Vehicle (var registration_number:String)

    companion object{


        fun getDataForSettingsVehiclesFragmentFromApi(token:String):ArrayList<VehicleHandler.Vehicle>{
            println("Debug : Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/vechicles"

            val client = OkHttpClient()

            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()

            println("Debug: Data access succesful URL: $url")

            val body = response.body()?.string()
            println("Debug: Pobietanie pojazdow $body")
            println("Debug: ${response.code()}")
            println("Debug: ${response.message()}")

            val json = JSONObject(body)
            var vehicleList = ArrayList<Vehicle>()
            var jsonArray_Vehicles = json.getJSONArray("vehicles")

            for (i in 0 until jsonArray_Vehicles.length()) {

                val vehicleObject = jsonArray_Vehicles.getJSONObject(i)
                vehicleList.add(
                    Vehicle(
                        vehicleObject.getString("registration_number")
                    )
                )
            }

            return vehicleList
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


        fun deleteVehicle(token: String, id:Int): Int{
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/vehicles/$id"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")


            val newRequest = Request.Builder().header("x-access-token", token).url(url).delete().build()

            val response = client.newCall(newRequest).execute()
            //communicationToServerStatus= response.code()
            println("Debug: ${response.code()}")

            return response.code()
        }

    }
}