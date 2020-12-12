package com.example.logisticky

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logisticky.viewLayer.ProductInfoFragment
import com.example.logisticky.viewLayer.ProductsFragment
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.system.measureTimeMillis

class ProductsHandler {


    data class ProductInfoFragmentBundle(var warehouses: ArrayList<ProductInfoFragment.Availability>, var jsonProductObject: JSONObject, var responseCode:Int)

    companion object {

        fun getDataForProductInfoFragmentFromApi(token:String, productId: Int): ProductInfoFragmentBundle? {
            var productInfoBundle: ProductInfoFragmentBundle? = null
            println("Debug : Attempting to Fetch JSON")

            val url = "https://dystproapi.azurewebsites.net/products/$productId"

            val client = OkHttpClient()

            val request = Request.Builder().header("x-access-token", token).url(url).build()
            println("Debug: token in fetchJson $token")

            val response = client.newCall(request).execute()

                    println("Debug: Data access succesful URL: $url")

                    val body = response.body()?.string()
                    println("Debug: $body")

                    val json = JSONObject(body)
                    val json_Product = json.getJSONObject("product")


                    var jsonArray_warehouseAvailability = json_Product.getJSONArray("availability")
                    val warehouseList = ArrayList<ProductInfoFragment.Availability>()
                    // Wyciąganie listy Availability z JSONObject("product")
                    for (i in 0 until jsonArray_warehouseAvailability.length()) {

                        val warehouseObject = jsonArray_warehouseAvailability.getJSONObject(i)
                        warehouseList.add(
                            ProductInfoFragment.Availability(
                                warehouseObject.getString("amount").toFloat(),
                                warehouseObject.getString("product_warehouse_id").toInt(),
                                warehouseObject.getString("warehouse_name")
                            )
                        )
                    }

                    productInfoBundle = ProductInfoFragmentBundle(warehouseList,json_Product,response.code())
                    return productInfoBundle
                }

        }

    }

