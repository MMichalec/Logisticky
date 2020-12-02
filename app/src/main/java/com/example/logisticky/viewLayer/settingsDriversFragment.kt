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
    var driverName: String = "empty"
    var driverSurname: String = "empty"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        testList.addAll( generateDummyList(15) as ArrayList<DriverItem>)
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
        displayList = testList

        recyclerView = view?.findViewById(R.id.drivers_recycleView)
        recyclerView?.adapter = DriverAdapter(displayList)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.setHasFixedSize(true)

        var editTextName = view.findViewById<EditText>(R.id.driverNameEditText)
        var editTextSurname = view.findViewById<EditText>(R.id.driverSurnameEditText)

        editTextName.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if(editTextName.text.isNotEmpty()){
                        driverName=editTextName.text.toString()


                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

        editTextName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {}
            else
            {
                if(editTextName.text.isNotEmpty()) driverName=editTextName.text.toString()
            }
        }

        editTextSurname.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS  -> {
                    if(editTextName.text.isNotEmpty()){
                        driverSurname=editTextSurname.text.toString()

                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

        editTextSurname.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {}
            else
            {
                if(editTextSurname.text.isNotEmpty()) driverSurname=editTextSurname.text.toString()
            }
        }



        view.findViewById<Button>(R.id.settingsRemoveDriverButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsAddDriverButton).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.settingsRemoveDriverButton -> {
                displayList.forEach {  if (it.isSelected) itemsToRemoveList.add(it) }
                itemsToRemoveList.forEach{ displayList.remove(it)}
                //TODO add removing items from database here
                refreshFragment()
            }

            R.id.settingsAddDriverButton -> {
                if (driverName != "empty" && driverSurname != "empty"){
                testList.add(DriverItem(driverName.capitalize(),driverSurname.capitalize(), checkBox = CheckBox(activity) ))
                    refreshFragment()
                    Toast.makeText(activity, "Added new driver ${driverName.capitalize()} ${driverSurname.capitalize()}", Toast.LENGTH_LONG).show()
                    view?.findViewById<EditText>(R.id.driverSurnameEditText)?.text?.clear()
                    view?.findViewById<EditText>(R.id.driverNameEditText)?.text?.clear()
            }
                else{
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
    private fun generateDummyList(size: Int): List<DriverItem>{
        val list = ArrayList<DriverItem>()

        val checkBox = CheckBox(activity)
        for (i in 0 until size){
            val item = DriverItem("Name $i","Surname $i", checkBox)
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