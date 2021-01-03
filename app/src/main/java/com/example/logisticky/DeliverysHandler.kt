package com.example.logisticky

import com.example.logisticky.viewLayer.ProductInfoFragment
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DeliverysHandler {

    data class CartFragmentBundle(var responseCode:Int, var cartProductsItemList: ArrayList<CartProductItem>)
    data class CartProductItem (var reservationId:Int, val productWarehouseId: Int, val productId: Int, val amount: Int, val price:Float, val productName:String, var warehouseName:String )
    data class DeliverysData (var deliveryId:Int, var state:String, var date:String, var pickupDate:String)
    data class DeliverysFragmentBundle (var responseCode:Int, var deliverysList: ArrayList<DeliverysData>)
    data class DeliveryProduct (val name:String, val amount:Int, val price: Float)
    data class DeliveryInfoFragmentBundle (val date:String, val driverName:String, val driverSurname:String, val vehiclePlateNumber: String, var  productsList: ArrayList<DeliveryProduct>)

    companion object {
        fun getCartList (token:String):CartFragmentBundle {

            var responseCode = 0

            println("Debug : Attempting to Fetch JSON from DeliverysFragment")
            val url = "https://dystproapi.azurewebsites.net/reservations"
            val client = OkHttpClient()


            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()

            responseCode = response.code()

            val body = response.body()?.string()
            println("Debug: $body")
            println("Debug: CartFragment $responseCode")

            val json = JSONObject(body)
            var jsonArray_cartProductsList = json.getJSONArray("reservations")

            val cartList = ArrayList<CartProductItem>()

            for (i in 0 until jsonArray_cartProductsList.length()) {

                val cartObject = jsonArray_cartProductsList.getJSONObject(i)
                cartList.add(
                    CartProductItem(
                        cartObject.getString("reservation_id").toInt(),
                        cartObject.getString("product_warehouse_id").toInt(),
                        cartObject.getString("product_id").toInt(),
                        cartObject.getString("amount").toInt(),
                        cartObject.getString("price").toFloat(),
                        cartObject.getString("name"),
                        cartObject.getString("warehouse_name")

                    )
                )
            }

            var bundle = CartFragmentBundle(responseCode,cartList)
            return bundle
        }

        fun addProductToCart(token: String, productWarehouseId: Int, amount: Int){


            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/reservations"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")

            val cartObject = JSONObject()
            val cartDataObject = JSONObject()


            cartDataObject.put("product_warehouse_id", productWarehouseId)
            cartDataObject.put("amount", amount)

            cartObject.put("reservation", cartDataObject)


            println("Debug: ${cartObject}")


            val body: RequestBody = RequestBody.create(JSON, cartObject.toString())
            val newRequest = Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()
            println("Debug: ${response.code()}")
            println("Debug: ${response.body()}")
            println("Debug: ${response.message()}")

        }

        fun removeProductFromCart(token: String, id:Int): Int{
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/reservations/$id"

            val client = OkHttpClient()


            val newRequest = Request.Builder().header("x-access-token", token).url(url).delete().build()

            val response = client.newCall(newRequest).execute()
            println("Debug: ${response.code()}")
            println("Debug: ${response.body()}")
            println("Debug: ${response.message()}")

            return response.code()
        }

        fun addDelivery(token:String, driverId:Int, vehicleId:Int,pickupDate: String?, reservationsList:ArrayList<Int>):Int {

            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/dispatches"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")


            val deliveryReservationsList = JSONArray(reservationsList)
            val fullDeliveryObject = JSONObject()
            val deliveryObject = JSONObject()

            deliveryObject.put("driver_id", driverId)
            deliveryObject.put("vehicle_id", vehicleId)
            if(pickupDate!=null){
                deliveryObject.put("pickup_planned_date", pickupDate)
            }
            deliveryObject.put("reservations_ids", deliveryReservationsList )

            fullDeliveryObject.put("dispatch", deliveryObject)


            println("Debug: Check me out here${fullDeliveryObject}")


            val body: RequestBody = RequestBody.create(JSON, fullDeliveryObject.toString())
            val newRequest = Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()
            println("Debug:  ${response.code()}")
            println("Debug:  ${response.body().toString()}")
            println("Debug: ${response.message()}")


//            val json = JSONObject ("${response.body().toString()}")
//            val dispatchObject = json.getJSONObject("dispatch")
//            val dispatchId = dispatchObject.getString("dispatch_id")



            return response.code()
        }

        fun getAllDeliverys(token: String):DeliverysFragmentBundle{

            val url = "https://dystproapi.azurewebsites.net/dispatches"
            val client = OkHttpClient()


            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()


            val body = response.body()?.string()
            println("Debug: $body")


            val json = JSONObject(body)
            var jsonArray_deliverysList = json.getJSONArray("dispatches")

            val deliverysList = ArrayList<DeliverysData>()

            for (i in 0 until jsonArray_deliverysList.length()) {

                val deliveryObject = jsonArray_deliverysList.getJSONObject(i)
                deliverysList.add(
                    DeliverysData(
                        deliveryObject.getString("dispatch_id").toInt(),
                        deliveryObject.getString("state"),
                        deliveryObject.getString("date"),
                        deliveryObject.getString("pickup_planned_date")

                    )
                )
            }

            val bundle = DeliverysFragmentBundle(response.code(), deliverysList)
            return bundle
        }

        fun getDeliveryInfo(token: String, deliveryId: Int): DeliveryInfoFragmentBundle{
            val url = "https://dystproapi.azurewebsites.net/dispatches/$deliveryId"
            val client = OkHttpClient()


            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()


            val body = response.body()?.string()
            println("Debug: $body")


            val dataFromJson = JSONObject(body)
            val json = dataFromJson.getJSONObject("dispatch")
            var jsonArray_productsList = json.getJSONArray("dispatched_products")

            val deliverysList = ArrayList<DeliveryProduct>()

            for (i in 0 until jsonArray_productsList.length()) {

                val deliveryObject = jsonArray_productsList.getJSONObject(i)
                deliverysList.add(
                    DeliveryProduct(
                        deliveryObject.getString("name"),
                        deliveryObject.getString("amount").toInt(),
                        deliveryObject.getString("price").toFloat()

                    )
                )
            }

            //var deliverysList = ArrayList<DeliveryProduct>()

            val jsonPickupDate = json.getString("pickup_planned_date")

            val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
            val offsetDateTime: OffsetDateTime = OffsetDateTime.parse(jsonPickupDate, timeFormatter)
            val cal = Calendar.getInstance()
            val date = Date.from(Instant.from(offsetDateTime))
            cal.time = date

            val dateString = "Due date: ${jsonPickupDate.take(10)}, ${String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)
                )
            }"


            //tu dobrze pokazuje czas

            val jsonDriver = json.getJSONObject("driver")
            val jsonDriverName = jsonDriver.getString("name")
            val jsonDriverSurname = jsonDriver.getString("surname")

            val jsonVehicle = json.getJSONObject("vehicle")
            val jsonVehiclePlateNumber = jsonVehicle.getString("registration_number")

            var bundle = DeliveryInfoFragmentBundle (dateString,jsonDriverName, jsonDriverSurname, jsonVehiclePlateNumber, deliverysList)
            return bundle;
        }

        fun deleteDelivery(token: String, id:Int): Int{
            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/dispatches/$id"

            val client = OkHttpClient()

            val newRequest = Request.Builder().header("x-access-token", token).url(url).delete().build()

            val response = client.newCall(newRequest).execute()
            //communicationToServerStatus= response.code()
            println("Debug: ${response.code()}")

            return response.code()
        }

        }


        }



