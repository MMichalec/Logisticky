package com.example.logisticky

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class VehicleHandler {

    data class Vehicle (var vehicleId:Int, var registration_number:String)

    companion object{


        fun getDataForSettingsVehiclesFragmentFromApi(token:String):ArrayList<VehicleHandler.Vehicle>{
            println("Debug : Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/vehicles"

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
                        vehicleObject.getString("vehicle_id").toInt(),
                        vehicleObject.getString("registration_number")
                    )
                )
            }

            return vehicleList
        }

        fun addVehicle(token:String, registration_number:String):Int{

            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/vehicles"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")

            val vehicleObject = JSONObject()

            val vehicleDataObject = JSONObject()


            vehicleDataObject.put("registration_number", registration_number)


            vehicleObject.put("vehicle", vehicleDataObject)


            println("Debug: ${vehicleObject}")


            val body: RequestBody = RequestBody.create(JSON, vehicleObject.toString())
            val newRequest = Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()
            //communicationToServerStatus= response.code()

            return response.code()
        }


        fun deleteVehicle(token: String, id:Int): Int{
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/vehicles/$id"

            val client = OkHttpClient()

            val newRequest = Request.Builder().header("x-access-token", token).url(url).delete().build()

            val response = client.newCall(newRequest).execute()
            println("Debug: ${response.code()}")

            return response.code()
        }

    }
}