package com.example.logisticky.viewLayer

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.logisticky.R
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsDefaultMagazineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsDefaultMagazineFragment : Fragment() {
    lateinit var spinnerMagazines: Spinner



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_settings_default_magazine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //get magazines from databases to this array VV
        val magazinesList: MutableList<String> = ArrayList()

        //PLACEHOLDERS
        magazinesList.add("LODZ")
        magazinesList.add("WARSAW")
        //PLACEHOLDERS

        val magazinesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, R.layout.spinner_item, magazinesList
        )
        magazinesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list)
        spinnerMagazines = view?.findViewById(R.id.settingsMagazinePicker) as Spinner
        spinnerMagazines.adapter = magazinesAdapter
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsDefaultMagazineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsDefaultMagazineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}