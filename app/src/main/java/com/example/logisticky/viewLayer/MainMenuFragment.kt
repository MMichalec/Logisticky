package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.logisticky.LoginActivity
import com.example.logisticky.MainActivity
import com.example.logisticky.R
import com.example.logisticky.TokenManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.net.SocketTimeoutException

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
    var token:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        token = TokenManager.loadData(this.requireActivity())
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Overwriting default back button action
            val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                showInfoDialog("Are you sure you want to close application?")
            }
            this.requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                callback
            )

            buttonState(false)
            changeButtonsColor(R.color.hint)

            super.onViewCreated(view, savedInstanceState)
            navController = Navigation.findNavController(view)

        //TODO Set animations somehow
        //Setting up cartFab Floating Action Button
        activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.setOnClickListener {
            navController.navigate(R.id.cartFragment)
        }

            view.findViewById<Button>(R.id.productsButton)?.setOnClickListener(this)
            view.findViewById<Button>(R.id.cartButton)?.setOnClickListener(this)
            view.findViewById<Button>(R.id.deliversButton)?.setOnClickListener(this)
            view.findViewById<Button>(R.id.logOutButton)?.setOnClickListener(this)
            view.findViewById<Button>(R.id.settingsButton)?.setOnClickListener(this)
            buttonState(false)

            CoroutineScope(IO).launch {
                val role = async {
                    token?.let { TokenManager.getPermissions(it) }
                }.await()

                activity?.runOnUiThread(object : Runnable {
                    override fun run() {
                        view.findViewById<ProgressBar>(R.id.mainMenuLoader).visibility = View.GONE
                    }
                })

                if (role?.roles?.size == 0) {
                    activity?.runOnUiThread(object : Runnable {
                        override fun run() {
                            showInfoDialog()
                        }
                    })
                } else {
                    activity?.runOnUiThread(object : Runnable {
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
            R.id.settingsButton -> navController.navigate(R.id.action_mainMenuFragment_to_settingsFragment)
            R.id.productsButton -> navController.navigate(R.id.action_mainMenuFragment_to_productsFragment)
            R.id.cartButton -> navController.navigate(R.id.action_mainMenuFragment_to_cartFragment)
            R.id.deliversButton -> navController.navigate(R.id.action_mainMenuFragment_to_deliversFragment)
            R.id.logOutButton -> {
                //Restarting activity with resetting token to null value. If Token == null then from MainActivity will be loaded Login Activity
                this.activity?.let { TokenManager.saveData(it,null) }
                MainActivity.isUserLogged = false
                val intent = Intent(activity, MainActivity::class.java)

                startActivity(intent);
                activity?.finish()
            }
        }
    }

    private fun showInfoDialog(){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle ("")
        builder.setTitle("Warning")
        builder.setMessage ("Looks like you don't have any permission yet. Please contact administrator or send request from Settings -> Account Permissions")
        builder.setPositiveButton("Make request now", {dialogInterface: DialogInterface, i: Int -> navController.navigate(R.id.action_mainMenuFragment_to_settingsVersionFragment)})
        builder.setNeutralButton("Close",{ dialogInterface: DialogInterface, i: Int -> } )
        builder.show()
    }

    private fun buttonState (isEnabled:Boolean){
        view?.findViewById<Button>(R.id.productsButton)?.isEnabled=isEnabled
        view?.findViewById<Button>(R.id.cartButton)?.isEnabled=isEnabled
        view?.findViewById<Button>(R.id.deliversButton)?.isEnabled=isEnabled
    }

    private fun changeButtonsColor (colorId : Int){

        view?.findViewById<Button>(R.id.productsButton)?.setBackgroundColor(resources.getColor(colorId))
        view?.findViewById<Button>(R.id.cartButton)?.setBackgroundColor(resources.getColor(colorId))
        view?.findViewById<Button>(R.id.deliversButton)?.setBackgroundColor(resources.getColor(colorId))
    }

    private fun showInfoDialog(message: String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle("")
        builder.setMessage(message)
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            activity?.finish()
        }
        builder.setNeutralButton("Cancel") {dialogInterface: DialogInterface, i: Int ->
        }
        builder.show()
    }

}