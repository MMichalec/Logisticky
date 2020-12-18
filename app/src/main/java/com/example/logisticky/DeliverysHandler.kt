package com.example.logisticky

import com.example.logisticky.viewLayer.ProductInfoFragment
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class DeliverysHandler {

    data class CartFragmentBundle(var responseCode:Int, var cartProductsItemList: ArrayList<CartProductItem>)
    data class CartProductItem (var reservationId:Int, val productWarehouseId: Int, val productId: Int, val amount: Int, val price:Float, val productName:String, var warehouseName:String )

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


    }


}