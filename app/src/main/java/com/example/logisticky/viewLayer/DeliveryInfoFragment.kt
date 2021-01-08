package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


// TODO: Change recycler view when in edit mode (with button mode), implement proper date picker (with dialog pop-up)

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeliveryInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeliveryInfoFragment : Fragment(), View.OnClickListener {


   private lateinit var recyclerView: RecyclerView
   private lateinit var navController: NavController
   private lateinit var currentDriver:String
   private lateinit var currentVehicle:String
   private lateinit var deliveryId:String
   private lateinit var warehouseName:String
   private var dateString = ""
   private var totalPrice: Float= 0.0F
   private var testList = ArrayList<CartItem>()
   private var token:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = this.activity?.let { TokenManager.loadData(it) }
        deliveryId = requireArguments().getString("deliveryId").toString()

         //TODO: Get warehouse from dispatch
        //warehouseName = requireArguments().getString("warehouseName").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_delivery_info, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        CoroutineScope(Dispatchers.IO).launch {
            val deliveryInfoFromApi = async {
                testList.clear()
                token?.let { DeliverysHandler.getDeliveryInfo(it, deliveryId.toInt()) }

            }.await()

            currentDriver = "${deliveryInfoFromApi!!.driverName} ${deliveryInfoFromApi.driverSurname}"
            currentVehicle= deliveryInfoFromApi.vehiclePlateNumber
            dateString = deliveryInfoFromApi.date

            deliveryInfoFromApi.productsList.forEach{
                testList.add(CartItem(it.name, "${String.format("%.2f", it.price )} PLN", "Amount: ${it.amount} p. |",0,
                    CheckBox(activity), false
                ))
                totalPrice += it.price
            }


            updateUI()
        }


        view.findViewById<Button>(R.id.deliveryCancel).setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.deliveryCancel -> {
                showInfoDialog("Are you sure you want to delete delivery $deliveryId")
            }
        }
    }




    private fun updateUI(){

        activity?.runOnUiThread(object: Runnable {
            override fun run() {

                view?.findViewById<ProgressBar>(R.id.deliveryInfoLoader)?.visibility = View.GONE

                view?.findViewById<TextView>(R.id.deliveryAmountText)?.text = "${String.format("%.2f", totalPrice )} PLN"
                view?.findViewById<TextView>(R.id.deliveryId)?.text ="Delivery ID: $deliveryId"
                view?.findViewById<TextView>(R.id.deliveryDriver)?.text = "Driver: $currentDriver"
                view?.findViewById<TextView>(R.id.deliveryVehicle)?.text = "Vehicle: $currentVehicle"
                view?.findViewById<TextView>(R.id.deliveryDate)?.text = dateString
                view?.findViewById<TextView>(R.id.deliveryMagazinePicker)?.text = "PUT WAREHOUSE NAME HERE"

                recyclerView = view?.findViewById(R.id.delivery_recycleView)!!
                recyclerView.adapter = ProductsAdapter3(testList)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)

            }
        })



    }


    private fun showInfoDialog(message: String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle("")
        builder.setMessage(message)
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            CoroutineScope(Dispatchers.IO).launch {
                val dataFromApi = async {
                    testList.clear()
                    token?.let { DeliverysHandler.deleteDelivery(it, deliveryId.toInt()) }

                }.await()
                println ("Debug: Deleting delivery response code $dataFromApi")
            }
        }
        builder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
        }
        builder.show()
    }

}