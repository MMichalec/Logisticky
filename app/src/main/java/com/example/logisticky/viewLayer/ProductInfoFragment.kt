package com.example.logisticky.viewLayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.example.logisticky.R
import kotlin.math.abs
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductInfoFragment : Fragment() {
    lateinit var productId:String
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        productId = requireArguments().getString("productId").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_product_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.findViewById<TextView>(R.id.productName)?.text = productId

        val packingValue = 10.75

        val editTextAddAmount = view.findViewById<EditText>(R.id.productAddAmount)
        val editTextAddPackage = view.findViewById<EditText>(R.id.productAddPackage)



        editTextAddAmount?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if(editTextAddAmount.text.isNotEmpty()){
                        val calculatedPackage = editTextAddAmount.text.toString().toDouble()
                        val roundedPackage = (calculatedPackage/packingValue).roundToInt()
                        val roundedAmount = roundedPackage * packingValue

                        if (calculatedPackage-roundedAmount > 0.001)
                        Toast.makeText(activity, "Value has been rounded", Toast.LENGTH_LONG).show()

                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                        editTextAddAmount.setText(roundedAmount.toString())
                        editTextAddPackage.setText(roundedPackage.toString())
                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })

        editTextAddPackage?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                    if(editTextAddPackage.text.isNotEmpty()){
                        val calculatedPackage = editTextAddPackage.text.toString().toDouble()
                        editTextAddAmount.setText((calculatedPackage*packingValue).toString())


                        //hide keyboard after input
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                    }
                    return@OnEditorActionListener true
                }
            }
            false
        })



        val spinnerArray: MutableList<String> = ArrayList()
        spinnerArray.add("LODZ")
        spinnerArray.add("WARSAW")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, R.layout.spinner_item, spinnerArray
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_list)
        val sItems = view.findViewById(R.id.magazinePicker) as Spinner
        sItems.adapter = adapter

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}