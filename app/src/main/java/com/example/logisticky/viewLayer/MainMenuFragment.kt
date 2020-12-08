package com.example.logisticky.viewLayer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.logisticky.LoginActivity
import com.example.logisticky.MainActivity
import com.example.logisticky.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment(), View.OnClickListener {

    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.settingsButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.productsButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.cartButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.deliversButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.logOutButton).setOnClickListener  (this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.settingsButton -> navController!!.navigate(R.id.action_mainMenuFragment_to_settingsFragment)
            R.id.productsButton -> navController!!.navigate(R.id.action_mainMenuFragment_to_productsFragment)
            R.id.cartButton -> navController!!.navigate(R.id.action_mainMenuFragment_to_cartFragment)
            R.id.deliversButton -> navController!!.navigate(R.id.action_mainMenuFragment_to_deliversFragment)
            R.id.logOutButton -> {
                MainActivity.globalVar = false
                startActivity(activity?.intent)
            }
        }
    }



}