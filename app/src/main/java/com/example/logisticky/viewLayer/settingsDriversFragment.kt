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
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import android.widget.TextView.OnEditorActionListener
import com.example.logisticky.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [settingsDriversFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class settingsDriversFragment : Fragment(), View.OnClickListener {
    var testList = ArrayList<DriverItem>()
    var displayList = ArrayList<DriverItem>()
    val itemsToRemoveList = ArrayList<DriverItem>()
    lateinit var recyclerView: RecyclerView
    var driverListFromAPI = ArrayList<DriversHandler.Driver>()

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

        token = this.activity?.let { TokenManager.loadData(it) }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_drivers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        CoroutineScope(Dispatchers.IO).launch {

                val dataFromAPI = async {
                    token?.let { DriversHandler.getDataForSettingsDriversFragmentFromApi(it) }

                }.await()

            if (dataFromAPI != null) {
                driverListFromAPI = dataFromAPI
            }
            val checkBox = CheckBox(activity)
            dataFromAPI?.forEach{
                testList.add(DriverItem(it.name,it.surname,checkBox))
            }
                updateSettingsDriversFragmentUI()
            }

        view.findViewById<Button>(R.id.settingsRemoveDriverButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsAddDriverButton).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.settingsRemoveDriverButton -> {

                displayList.forEach {  if (it.isSelected) itemsToRemoveList.add(it) }

                driverListFromAPI.forEach{

                    for (i in 0 until itemsToRemoveList.size){

                        if (it.name == itemsToRemoveList[i].name && it.surname == itemsToRemoveList[i].surname){
                            var idToDelete = it.driver_id
                            CoroutineScope(Dispatchers.IO).launch {

                                val dataFromAPI2 = async {
                                    token?.let { DriversHandler.deleteDriver(it,idToDelete ) }
                                }.await()

                                println("Debug: Driver deletede code: $dataFromAPI2")
                                updateSettingsDriversFragmentUI()
                            }
                        }
                    }
                }

                itemsToRemoveList.forEach{ displayList.remove(it)}
                activity?.runOnUiThread(object : Runnable {
                    override fun run() {
                        updateSettingsDriversFragmentUI()
                    }
                })

            }

            R.id.settingsAddDriverButton -> {

                var editTextName = view?.findViewById<EditText>(R.id.driverNameEditText)?.text.toString().capitalize()
                var editTextSurname = view?.findViewById<EditText>(R.id.driverSurnameEditText)?.text.toString().capitalize()

                if (editTextName != "" && editTextSurname != ""){

                    activity?.runOnUiThread(object : Runnable {
                        override fun run() {
                            testList.add(DriverItem(editTextName,editTextSurname, checkBox = CheckBox(activity) ))
                            CoroutineScope(Dispatchers.IO).launch {

                                val dataFromAPI = async {
                                    token?.let { DriversHandler.addDriver(it, editTextName, editTextSurname) }
                                }.await()
                                updateSettingsDriversFragmentUI()
                            }

                        }
                    })

                    //TODO zrobić tak zebyt mozna bylo kasowac kierowcow zaraz po dodaniu - musze zaktualizowac listeKierowcowZApi

                    Toast.makeText(activity, "Added new driver ${editTextName} ${editTextSurname}", Toast.LENGTH_LONG).show()
                    view?.findViewById<EditText>(R.id.driverSurnameEditText)?.text?.clear()
                    view?.findViewById<EditText>(R.id.driverNameEditText)?.text?.clear()
            } else{
                    Toast.makeText(activity, "Invalid name or surname input. Make sure you accepted inputed text.", Toast.LENGTH_LONG).show()
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
         * @return A new instance of fragment settingsDriversFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            settingsDriversFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun updateSettingsDriversFragmentUI (){
        activity?.runOnUiThread(object : Runnable {
            override fun run() {

                displayList = testList
                recyclerView = view?.findViewById(R.id.drivers_recycleView)!!
                recyclerView?.adapter = DriverAdapter(displayList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)
                view?.findViewById<ProgressBar>(R.id.driversLoader)?.visibility = View.GONE
            }
        })

    }
}