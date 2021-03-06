package com.example.logisticky.viewLayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.logisticky.R
import com.example.logisticky.TokenManager
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
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), View.OnClickListener {
    lateinit var navController: NavController
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonState(false)
        changeButtonsColor(R.color.hint)

        token = this.activity?.let { TokenManager.loadData(it) }

        navController = Navigation.findNavController(view)
        //view.findViewById<Button>(R.id.settingsMagazineButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsDiscountsButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsVersionButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsDriversButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.settingsVehiclesButtons).setOnClickListener(this)



        CoroutineScope(Dispatchers.IO).launch {
            val role = async {
                token?.let { TokenManager.getPermissions(it) }
            }.await()

            activity?.runOnUiThread(object: Runnable {
                override fun run() {
                    view.findViewById<ProgressBar>(R.id.settingsLoader).visibility = View.GONE
                }
            })
            if(role?.roles?.size == 0){

                activity?.runOnUiThread(object: Runnable {
                    override fun run() {
                    }
                })
            }else {
                activity?.runOnUiThread(object: Runnable {
                    override fun run() {
                        buttonState(true)
                        changeButtonsColor(R.color.mainTile)

                    }
                })
            }
        }

    }


    override fun onClick(v: View?) {
        when(v!!.id){
           // R.id.settingsMagazineButton -> navController.navigate(R.id.action_settingsFragment_to_settingsDefaultMagazineFragment2)
            R.id.settingsDiscountsButton -> navController.navigate(R.id.action_settingsFragment_to_settingsDiscountFragment2)
            R.id.settingsVersionButton -> navController.navigate(R.id.action_settingsFragment_to_settingsVersionFragment)
            R.id.settingsDriversButton -> navController.navigate(R.id.action_settingsFragment_to_settingsDriversFragment)
            R.id.settingsVehiclesButtons -> navController.navigate(R.id.action_settingsFragment_to_settingsVehiclesFragment)
            }

        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun buttonState(isEnabled: Boolean){
        //view?.findViewById<Button>(R.id.settingsMagazineButton)?.isEnabled = isEnabled
        view?.findViewById<Button>(R.id.settingsDiscountsButton)?.isEnabled = isEnabled
        view?.findViewById<Button>(R.id.settingsDriversButton)?.isEnabled = isEnabled
        view?.findViewById<Button>(R.id.settingsVehiclesButtons)?.isEnabled = isEnabled
    }

    private fun changeButtonsColor (colorId : Int){
        //view?.findViewById<Button>(R.id.settingsMagazineButton)?.setBackgroundColor(resources.getColor(colorId))
        view?.findViewById<Button>(R.id.settingsDiscountsButton)?.setBackgroundColor(resources.getColor(colorId))
        view?.findViewById<Button>(R.id.settingsDriversButton)?.setBackgroundColor(resources.getColor(colorId))
        view?.findViewById<Button>(R.id.settingsVehiclesButtons)?.setBackgroundColor(resources.getColor(colorId))
    }



}