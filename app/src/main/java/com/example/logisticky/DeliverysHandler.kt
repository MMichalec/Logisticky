package com.example.logisticky

import com.example.logisticky.viewLayer.ProductInfoFragment
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class DeliverysHandler {

    data class CartFragmentBundle(var responseCode:Int, var cartProductsItemList: ArrayList<CartProductItem>)
    data class CartProductItem (var reservationId:Int, val productWarehouseId: Int, val productId: Int, val amount: Int, val price:Float, val productName:String, var warehouseName:String )
    data class DeliverysData (var deliveryId:Int, var state:String, var date:String, var pickupDate:String)
    data class DeliverysFragmentBundle (var responseCode:Int, var deliverysList: ArrayList<DeliverysData>)

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

        fun addDelivery(token:String, driverId:Int, vehicleId:Int, reservationsList:ArrayList<Int>):Int {

            println("Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/dispatches"

            val client = OkHttpClient()

            val JSON = MediaType.parse("application/json;charset=utf-8")


            val deliveryReservationsList = JSONArray(reservationsList)
            val fullDeliveryObject = JSONObject()
            val deliveryObject = JSONObject()

            deliveryObject.put("driver_id", driverId)
            deliveryObject.put("vehicle_id", vehicleId)
            deliveryObject.put("reservations_ids", deliveryReservationsList )

            fullDeliveryObject.put("dispatch", deliveryObject)


            println("Debug: ${fullDeliveryObject}")


            val body: RequestBody = RequestBody.create(JSON, fullDeliveryObject.toString())
            val newRequest = Request.Builder().header("x-access-token", token).url(url).post(body).build()

            val response = client.newCall(newRequest).execute()
            println("Debug: ${response.code()}")
            println("Debug: ${response.body()}")
            println("Debug: ${response.message()}")

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



        }

    }
