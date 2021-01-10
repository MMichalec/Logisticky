package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MakeDeliveryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MakeDeliveryFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    var testList = ArrayList<CartItem>()
    var displayList = ArrayList<CartItem>()
    var cartItemsList = ArrayList<DeliverysHandler.CartProductItem>()
    lateinit var recyclerView: RecyclerView
    lateinit var defaultDeliveryDate:Date

    var isoDateString: String?= null

    var totalPrice: Float= 0.0F
    lateinit var checkBox: CheckBox

    lateinit var navController: NavController

    var token:String? = null
    lateinit var defaultDriverSpinnerText:String
    lateinit var defaultVehicleSpinnerText:String

    lateinit var spinnerDrivers: Spinner
    //    lateinit var spinnerMagazines:Spinner
    lateinit var spinnerVehicles: Spinner

    var foundDriver = false
    var foundVehicle = false

    var firstDriverSwap = true
    var firstVehicleSwap = true

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    var fullDriversList = ArrayList<DriversHandler.Driver>()
    var fullVehiclesList = ArrayList<VehicleHandler.Vehicle>()

    //Had to initialize multiple variables because if I used one there were weird interactions when clicking spinner (visualy showed as all spinners clicked at once)
    lateinit var driverSpinnerBackground: Drawable
    lateinit var vehicleSpinnerBackground: Drawable
    //lateinit var magazineSpinnerBackground:Drawable

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var deliveryId:String
    lateinit var productsForDeliveryList: ArrayList<Int>
    lateinit var warehouseName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        checkBox = CheckBox(activity)
        token = this.activity?.let { TokenManager.loadData(it) }
        deliveryId = "NEW DELIVERY"
        warehouseName = requireArguments().getString("deliveryId").toString()
        productsForDeliveryList = requireArguments().getIntegerArrayList("reservationsIdList") as ArrayList<Int>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        pickDate()
        view.findViewById<RecyclerView>(R.id.delivery_recycleView).visibility = View.INVISIBLE

        //Get products from dbs by ids

            defaultDriverSpinnerText = "PICK DRIVER"
            defaultVehicleSpinnerText = "PICK VEHICLE"


        CoroutineScope(Dispatchers.IO).launch{

            var dataFromAPI = async {
                testList.clear()
                token?.let { DeliverysHandler.getCartList(it) }

            }.await()



            println(dataFromAPI?.responseCode)

            cartItemsList = dataFromAPI!!.cartProductsItemList

            dataFromAPI?.cartProductsItemList?.forEach{

                println("Debug: Product ${it.productName}, amount: ${it.amount}, ${it.warehouseName}")



                var dataFromApi2 = async {
                    getProductInfo(it.productId)
                }.await()

                val amount = dataFromApi2.getString("unit_number").toDouble() * it.amount
                val unit = dataFromApi2.getString("unit_name")


                productsForDeliveryList.forEach{ it2 ->

                    if (it.reservationId == it2)
                    {
                        testList.add(
                            CartItem(
                                it.productName,
                                "W: ${it.warehouseName}",
                                "Amount: ${it.amount} p. | $amount $unit,",
                                it.reservationId,
                                checkBox,
                                false
                            )
                        )
                        totalPrice += it.price
                    }
                }

            }
            updateUI(defaultDriverSpinnerText, defaultVehicleSpinnerText)
        }

        view.findViewById<Button>(R.id.deliveryCreate).setOnClickListener(this)
        view.findViewById<Button>(R.id.deliveryCancel).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.deliveryCreate -> {

                println(isoDateString)


                var pickedDriverString = defaultDriverSpinnerText
                var pickedDriverId = 0

                if (firstDriverSwap && spinnerDrivers.selectedItemPosition > 0) {

                    var spinnerPickedNumber = spinnerDrivers.selectedItemPosition - 1 as Int
                    spinnerDrivers.setSelection(spinnerPickedNumber)
                    pickedDriverString = spinnerDrivers.selectedItem.toString()
                    firstDriverSwap = false

                } else {
                    pickedDriverString = spinnerDrivers.selectedItem.toString()
                }


                var pickedVehicleString = defaultVehicleSpinnerText
                var pickedVehicleId = 0

                if (firstVehicleSwap && spinnerVehicles.selectedItemPosition > 0) {

                    var spinnerPickedNumber = spinnerVehicles.selectedItemPosition - 1 as Int
                    spinnerVehicles.setSelection(spinnerPickedNumber)
                    pickedVehicleString = spinnerVehicles.selectedItem.toString()
                    firstVehicleSwap = false

                } else {
                    pickedVehicleString = spinnerVehicles.selectedItem.toString()
                }

                if (pickedDriverString == defaultDriverSpinnerText || pickedVehicleString == defaultVehicleSpinnerText) {
                    showInfoDialog("Please pick driver and a vehicle.")
                } else {

                    fullDriversList.forEach {
                        if (pickedDriverString == "${it.name} ${it.surname}")
                            pickedDriverId = it.driver_id
                    }



                    fullVehiclesList.forEach {
                        if (pickedVehicleString == it.registration_number)
                            pickedVehicleId = it.vehicleId
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        val dataFromApi2 = async {
                            token?.let {
                                DeliverysHandler.addDelivery(
                                    it,
                                    pickedDriverId,
                                    pickedVehicleId,
                                    isoDateString,
                                    productsForDeliveryList
                                )
                            }

                        }.await()
                        println("Debug: Make Delivery code: $dataFromApi2")
                        //updateUI()
                    }
                    navController.navigate(R.id.action_makeDeliveryFragment_to_deliversFragment)
                }
            }

            R.id.deliveryCancel -> {
                navController.navigate(R.id.action_makeDeliveryFragment_to_cartFragment)
            }

            }

        }

    private fun updateUI(driverName: String, vehiclePlateNumber: String){


        activity?.runOnUiThread(object : Runnable {
            override fun run() {
                view?.findViewById<TextView>(R.id.deliveryId)?.text = deliveryId

                var dateFormat = SimpleDateFormat("dd/MM/yyyy");
                val currentDate = Date()
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_MONTH, 7)

                defaultDeliveryDate = calendar.time

                view!!.findViewById<TextView>(R.id.deliveryDatePicker).text = dateFormat.format(defaultDeliveryDate)

                //Disabled in view-only mode. Will be enabled in edit mode


//                //get magazines from databases to this array VV
//                val magazinesList: MutableList<String> = ArrayList()
//
//                //PLACEHOLDERS
//                magazinesList.add("LODZ")
//                magazinesList.add("WARSAW")
//                //PLACEHOLDERS
//
//                val magazinesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
//                    view?.context!!, R.layout.support_simple_spinner_dropdown_item, magazinesList
//                )
//                magazinesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//                spinnerMagazines = view?.findViewById(R.id.deliveryMagazinePicker) as Spinner
//                spinnerMagazines.adapter = magazinesAdapter

                view?.findViewById<TextView>(R.id.deliveryMagazinePicker)?.text = warehouseName


                //get drivers from databases to this array VV
                var driversList: MutableList<String> = ArrayList()

                //PLACEHOLDERS

                driversList = getAllDrivers()

                var ixa = driversList.indexOf("$driverName")
                println("Debug fuck spinners : $ixa ")
                driversList.add("$driverName")


                //PLACEHOLDERS

                val driversAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    view?.context!!, R.layout.support_simple_spinner_dropdown_item, driversList
                )
                driversAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinnerDrivers = view?.findViewById(R.id.deliveryDriverPicker) as Spinner
                spinnerDrivers.adapter = driversAdapter

                spinnerDrivers.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {

                            if (foundDriver) driversList.remove("$driverName")
                            else foundDriver = true
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }


                //get vehicle from databases to this array VV
                var vehiclesList: MutableList<String> = ArrayList()

                //PLACEHOLDERS
                vehiclesList = getAllVehicles()
                vehiclesList.add(vehiclePlateNumber)
                //PLACEHOLDERS

                val vehiclesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    view?.context!!, R.layout.support_simple_spinner_dropdown_item, vehiclesList
                )
                vehiclesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinnerVehicles = view?.findViewById(R.id.deliveryVehiclePicker) as Spinner
                spinnerVehicles.adapter = vehiclesAdapter
                spinnerVehicles.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            if (foundVehicle) vehiclesList.remove("$vehiclePlateNumber")
                            else foundVehicle = true

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }


                //Disabling spinners in view-only mode. Will be reenebling them when go into edit mode


                //Setting up recycler view

                //Populate this list with products for this delivery from DB VV










                displayList = testList

                view!!.findViewById<RecyclerView>(R.id.delivery_recycleView).visibility =
                    View.VISIBLE
                recyclerView = view?.findViewById(R.id.delivery_recycleView)!!
                recyclerView?.adapter = ProductsAdapter3(displayList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)
                view!!.findViewById<TextView>(R.id.deliveryAmountText).text = "${
                    String.format(
                        "%.2f",
                        totalPrice
                    )
                } PLN"


                driverSpinnerBackground = spinnerDrivers.getBackground()
                vehicleSpinnerBackground = spinnerVehicles.getBackground()
//                magazineSpinnerBackground = spinnerMagazines.getBackground()

            }

        })



    }

    private fun getDateTimeCalendar(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }



    private fun pickDate(){

        var datePicker = view?.findViewById<EditText>(R.id.deliveryDatePicker)
        datePicker?.setOnTouchListener(View.OnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                getDateTimeCalendar()
                DatePickerDialog(v.context, this, year, month, day).show()
            }
            false
        })
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        val formatedDay = String.format("%02d", p3)
        savedMonth = p2+1
        val formatedMonth = String.format("%02d", p2 + 1)
        savedYear = p1

        getDateTimeCalendar()

        isoDateString = "${savedYear}-${formatedMonth}-${formatedDay}T23:59:00.000Z"

        val currentDateTime = LocalDateTime.now()
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
        val offsetDateTime: OffsetDateTime = OffsetDateTime.parse(isoDateString, timeFormatter)
        val offsetDateTime2: OffsetDateTime = OffsetDateTime.parse(currentDateTime.toString()+"Z", timeFormatter)
        val cal = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        val date = Date.from(Instant.from(offsetDateTime))
        val date2 = Date.from(Instant.from(offsetDateTime2))
        cal.time = date
        cal2.time = date2

        if(cal.time.before(cal2.time))
        {
            showInfoDialog("Date can't be from the past.")
            isoDateString=null
        }else {
            view?.findViewById<EditText>(R.id.deliveryDatePicker)?.setText("$formatedDay/$formatedMonth/$savedYear")
            isoDateString = "${savedYear}-${formatedMonth}-${formatedDay}T22:59:00.000Z"
        }



        println("Debug: Iso date string $isoDateString")

    }

    private fun getAllDrivers():ArrayList<String>{

        val driversList = ArrayList<String>()

        CoroutineScope(Dispatchers.IO).launch {

            val dataFromAPI = async {
                token?.let { DriversHandler.getDataForSettingsDriversFragmentFromApi(it) }

            }.await()

            if (dataFromAPI != null) {
                dataFromAPI?.forEach{
                    if("${it.name} ${it.surname}" != defaultDriverSpinnerText)
                        driversList.add("${it.name} ${it.surname}")
                }
            }
            if (dataFromAPI != null) {
                fullDriversList = dataFromAPI
            }
        }
        return driversList
    }

    private fun getAllVehicles():ArrayList<String>{

        val vehiclesList = ArrayList<String>()

        CoroutineScope(Dispatchers.IO).launch {

            val dataFromAPI = async {
                token?.let { VehicleHandler.getDataForSettingsVehiclesFragmentFromApi(it) }

            }.await()

            if (dataFromAPI != null) {
                dataFromAPI?.forEach{
                    if(it.registration_number != defaultVehicleSpinnerText)
                        vehiclesList.add(it.registration_number)
                }
            }
            if (dataFromAPI != null) {
                fullVehiclesList = dataFromAPI
            }
        }
        return vehiclesList
    }

    private fun getProductInfo(productId: Int): JSONObject {
        lateinit var dataJsonProduct: JSONObject
        val dataFromAPI = token?.let { ProductsHandler.getDataForProductInfoFragmentFromApi(
            it,
            productId
        ) }
        dataJsonProduct = dataFromAPI!!.jsonProductObject
        return dataJsonProduct
    }

    private fun showInfoDialog(message: String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle("")
        builder.setMessage(message)
        builder.setPositiveButton("Okay") { dialogInterface: DialogInterface, i: Int ->
        }
        builder.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MakeDeliveryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MakeDeliveryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}