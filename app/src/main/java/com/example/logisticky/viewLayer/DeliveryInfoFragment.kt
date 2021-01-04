package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
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
import org.json.JSONObject
import java.util.*
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

    var totalPrice: Float= 0.0F
    lateinit var navController: NavController
    var dateString = ""

    var testList = ArrayList<CartItem>()
    lateinit var recyclerView: RecyclerView
    var token:String? = null
    lateinit var currentDriver:String
    lateinit var currentVehicle:String



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var deliveryId:String
    lateinit var warehouseName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        token = this.activity?.let { TokenManager.loadData(it) }
        deliveryId = requireArguments().getString("deliveryId").toString()

         //TODO: Get warehouse from dispatch
        //warehouseName = requireArguments().getString("warehouseName").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_info, container, false)




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        CoroutineScope(Dispatchers.IO).launch {
            val dataFromApi = async {
                testList.clear()
                token?.let { DeliverysHandler.getDeliveryInfo(it, deliveryId.toInt()) }

            }.await()

            currentDriver = "${dataFromApi!!.driverName} ${dataFromApi.driverSurname}"
            currentVehicle= dataFromApi.vehiclePlateNumber
            dateString = dataFromApi.date

            dataFromApi.productsList.forEach{
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

                //Disabled in view-only mode. Will be enabled in edit mode




                view?.findViewById<TextView>(R.id.deliveryMagazinePicker)?.text = "PUT WAREHOUSE NAME HERE"

                recyclerView = view?.findViewById(R.id.delivery_recycleView)!!
                recyclerView?.adapter = ProductsAdapter3(testList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)



            }

        })



    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeliveryInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeliveryInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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