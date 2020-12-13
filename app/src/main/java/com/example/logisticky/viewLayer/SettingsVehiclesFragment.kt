package com.example.logisticky.viewLayer

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsVehiclesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsVehiclesFragment : Fragment(), View.OnClickListener {

    var testList = ArrayList<VehicleItem>()
    var displayList = ArrayList<VehicleItem>()
    val itemsToRemoveList = ArrayList<VehicleItem>()
    lateinit var recyclerView: RecyclerView
    var vehiclePlateNumber: String = "empty"

    var token:String? = null


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        testList.addAll( generateDummyList(15) as ArrayList<VehicleItem>)
        token = this.activity?.let { TokenManager.loadData(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_vehicles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {

            val dataFromAPI = async {
                token?.let { VehicleHandler.getDataForSettingsVehiclesFragmentFromApi(it) }

            }.await()


            }



        displayList = testList

        recyclerView = view?.findViewById(R.id.vehicles_recycleView)
        recyclerView?.adapter = VehicleAdapter(displayList)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.setHasFixedSize(true)

        var editTextPlateNumber = view.findViewById<EditText>(R.id.vehicleEditText)

        editTextPlateNumber.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if(editTextPlateNumber.text.isNotEmpty()){
                        vehiclePlateNumber=editTextPlateNumber.text.toString()


                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

        editTextPlateNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {}
            else
            {
                if(editTextPlateNumber.text.isNotEmpty()) vehiclePlateNumber=editTextPlateNumber.text.toString()
            }
        }

        view.findViewById<Button>(R.id.settingsAddVehicleButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsRemoveVehicleButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.settingsRemoveVehicleButton -> {
                displayList.forEach {  if (it.isSelected) itemsToRemoveList.add(it) }
                itemsToRemoveList.forEach{ displayList.remove(it)}
                //TODO add removing items from database here
                refreshFragment()
            }

            R.id.settingsAddVehicleButton -> {
                if (vehiclePlateNumber != "empty"){
                    testList.add(VehicleItem(vehiclePlateNumber.toUpperCase(), checkBox = CheckBox(activity) ))
                    refreshFragment()
                    Toast.makeText(activity, "Added new vehicle: ${vehiclePlateNumber.toUpperCase()}", Toast.LENGTH_LONG).show()
                    view?.findViewById<EditText>(R.id.vehicleEditText)?.text?.clear()

                }
                else{
                    Toast.makeText(activity, "Invalid vehicle plate number input. Make sure you accepted inputed text.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsVehiclesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsVehiclesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun generateDummyList(size: Int): List<VehicleItem>{
        val list = ArrayList<VehicleItem>()

        val checkBox = CheckBox(activity)
        for (i in 0 until size){
            val item = VehicleItem("Plate $i", checkBox)
            list +=item
        }
        return list
    }

    fun refreshFragment(){
        val ft = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }
}

