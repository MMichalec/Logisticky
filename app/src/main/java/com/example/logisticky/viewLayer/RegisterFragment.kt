package com.example.logisticky.viewLayer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.logisticky.R
import com.example.logisticky.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment(), View.OnClickListener {
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.registerSendButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.registerSendButton -> {
                val id = view?.findViewById<EditText>(R.id.registerEmailEditText)?.text.toString()
                val pw = view?.findViewById<EditText>(R.id.registerPasswordEditText)?.text.toString()

                CoroutineScope(IO).launch {

                    val responseCode =  async {
                        TokenManager.register(id,pw)
                    }.await()

                    activity?.runOnUiThread(object : Runnable {
                        override fun run() {
                            when (responseCode){
                                200 -> showInfoDialog("Regiester successful. You will be redirected to login page")
                                400 -> {
                                    showInfoDialog("Missing one of the arguments needed for registration")
                                }
                                422 -> {
                                    showInfoDialog("Password is too weak! Password has to be at least 8 characters long, have at least one big and small letter, special character and number.")
                                }
                            }
                        }
                    })


                }

            }
        }
        }

    fun showInfoDialog(message:String){
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle ("")
        builder.setMessage (message)
        builder.setPositiveButton("Okay",{ dialogInterface: DialogInterface, i: Int -> Toast.makeText(activity, "Okay!", Toast.LENGTH_LONG).show()} )
        builder.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}