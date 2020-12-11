package com.example.logisticky.viewLayer

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.logisticky.ProductItem
import com.example.logisticky.ProductsAdapter
import com.example.logisticky.R
import java.util.*


// TODO: Change recycler view when in edit mode (with button mode), implement proper date picker (with dialog pop-up)

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeliveryInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeliveryInfoFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    var isViewMode = true;
    var testList = ArrayList<ProductItem>()
    lateinit var recyclerView: RecyclerView
    lateinit var spinnerDrivers:Spinner
    lateinit var spinnerMagazines:Spinner
    lateinit var spinnerVehicles:Spinner

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    //Had to initialize multiple variables because if I used one there were weird interactions when clicking spinner (visualy showed as all spinners clicked at once)
    lateinit var driverSpinnerBackground:Drawable
    lateinit var vehicleSpinnerBackground:Drawable
    lateinit var magazineSpinnerBackground:Drawable

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var deliveryId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        deliveryId = requireArguments().getString("deliveryId").toString()

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
        pickDate()


        view.findViewById<TextView>(R.id.deliveryId)?.text =deliveryId
        view.findViewById<TextView>(R.id.deliveryDatePicker)?.text = "TU BEDZIE DATA ZAMOWIENIA!"

        //Disabled in view-only mode. Will be enabled in edit mode
        view.findViewById<TextView>(R.id.deliveryDatePicker).isEnabled=false

        //get magazines from databases to this array VV
        val magazinesList: MutableList<String> = ArrayList()

        //PLACEHOLDERS
        magazinesList.add("LODZ")
        magazinesList.add("WARSAW")
        //PLACEHOLDERS

        val magazinesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, R.layout.support_simple_spinner_dropdown_item, magazinesList
        )
        magazinesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerMagazines = view.findViewById(R.id.deliveryMagazinePicker) as Spinner
        spinnerMagazines.adapter = magazinesAdapter


        //get drivers from databases to this array VV
        val driversList: MutableList<String> = ArrayList()

        //PLACEHOLDERS
        driversList.add("Adam")
        driversList.add("Jacek")
        //PLACEHOLDERS

        val driversAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, R.layout.support_simple_spinner_dropdown_item, driversList
        )
        driversAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerDrivers = view.findViewById(R.id.deliveryDriverPicker) as Spinner
        spinnerDrivers.adapter = driversAdapter

        //get vehicle from databases to this array VV
        val vehiclesList: MutableList<String> = ArrayList()

        //PLACEHOLDERS
        vehiclesList.add("EL085YL")
        vehiclesList.add("EL3934Y")
        //PLACEHOLDERS

        val vehiclesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, R.layout.support_simple_spinner_dropdown_item, vehiclesList
        )
        vehiclesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerVehicles = view.findViewById(R.id.deliveryVehiclePicker) as Spinner
        spinnerVehicles.adapter = vehiclesAdapter

        //Disabling spinners in view-only mode. Will be reenebling them when go into edit mode




        //Setting up recycler view

        //Populate this list with products for this delivery from DB VV
        testList = generateDummyList(15) as ArrayList<ProductItem>;


        recyclerView = view?.findViewById(R.id.delivery_recycleView)
        recyclerView?.adapter = ProductsAdapter(testList)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.setHasFixedSize(true)


        view.findViewById<Button>(R.id.deliveryEditButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.deliverySaveEdit).setOnClickListener(this)

        driverSpinnerBackground = spinnerDrivers.getBackground()
        vehicleSpinnerBackground = spinnerVehicles.getBackground()
        magazineSpinnerBackground = spinnerMagazines.getBackground()



        updateMode()



    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.deliveryEditButton -> (changeMode())
            R.id.deliverySaveEdit -> (changeMode())

        }
    }

    fun changeMode() {
        if(isViewMode){
            isViewMode=false
            val toast = Toast.makeText(activity, "Edit mode enabled", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        }
        else if(!isViewMode) {
            isViewMode=true
            val toast = Toast.makeText(activity, "View mode enabled", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        }
        updateMode()
    }

    fun updateMode(){


        if (isViewMode){

            view?.findViewById<Button>(R.id.deliveryAddProduct)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.deliveryCancel)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.deliverySaveEdit)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.deliveryEditButton)?.visibility = View.VISIBLE

            spinnerDrivers.background = null;
            spinnerDrivers.isEnabled = false
            spinnerDrivers.isClickable = false


            spinnerVehicles.background = null;
            spinnerVehicles.isEnabled = false
            spinnerVehicles.isClickable = false

            spinnerMagazines.background = null;
            spinnerMagazines.isEnabled = false
            spinnerMagazines.isClickable = false

            view?.findViewById<TextView>(R.id.deliveryDatePicker)?.isEnabled = false


        }
        else{
            view?.findViewById<Button>(R.id.deliveryAddProduct)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.deliveryCancel)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.deliverySaveEdit)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.deliveryEditButton)?.visibility = View.GONE

            spinnerDrivers.background = driverSpinnerBackground
            spinnerDrivers.isEnabled = true
            spinnerDrivers.isClickable = true


            spinnerVehicles.background = vehicleSpinnerBackground
            spinnerVehicles.isEnabled = true
            spinnerVehicles.isClickable = true

            spinnerMagazines.background = magazineSpinnerBackground
            spinnerMagazines.isEnabled = true
            spinnerMagazines.isClickable = true

            view?.findViewById<TextView>(R.id.deliveryDatePicker)?.isEnabled = true
        }
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


    //PLACEHOLDER ITEMS
    private fun generateDummyList(size: Int): List<ProductItem>{
        val list = ArrayList<ProductItem>()

        val checkBox = CheckBox(activity)
        for (i in 0 until size){
            val item = ProductItem("Item $i", 0)
            list +=item
        }
        return list
    }

    private fun getDateTimeCalendar(){
        val cal:Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }



    private fun pickDate(){

        var datePicker = view?.findViewById<EditText>(R.id.deliveryDatePicker)
        datePicker?.setOnTouchListener(OnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action){
                getDateTimeCalendar()
                DatePickerDialog(v.context,this,year,month,day).show()
            }
            false
        })
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        savedMonth = p2
        savedYear = p1

        getDateTimeCalendar()

        view?.findViewById<EditText>(R.id.deliveryDatePicker)?.setText("$savedDay-$savedMonth-$savedYear")


    }


}