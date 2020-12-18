package com.example.logisticky.viewLayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
 * Use the [settingsVersionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class settingsVersionFragment : Fragment(),View.OnClickListener {
    var token: String? = null

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
        return inflater.inflate(R.layout.fragment_settings_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(IO).launch {

            val authMeData = async {
                println("test")
                token?.let { TokenManager.getPermissions(it) }

            }.await()

            var rolesString = ""
            activity?.runOnUiThread(object: Runnable{
                override fun run() {
                    view.findViewById<ProgressBar>(R.id.permissionsLoader).visibility = View.GONE
                    if (authMeData?.responseCode == 200){

                        println("Debug: this shit right here ${authMeData.roles?.size}")
                        if (authMeData.roles?.size != 0){
                            authMeData.roles?.forEach{
                                rolesString  = "$it\n "
                            }
                            view.findViewById<TextView>(R.id.settingsPermissions).text = rolesString
                        }else
                        view.findViewById<TextView>(R.id.settingsPermissions).text = "No role has been assigned yet."
                    }
                }

            })

        }

        view.findViewById<Button>(R.id.settingsPermissionsSendRequestButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.settingsPermissionsSendRequestButton -> {


                CoroutineScope(IO).launch {

                    val postResponseCode = async {
                        token?.let { TokenManager.postRequest(it, view?.findViewById<EditText>(R.id.settingsPermissionsRequestComment)?.text.toString()) }
                }.await()

                    activity?.runOnUiThread(object: Runnable {
                        override fun run() {
                            if(postResponseCode == 200){
                                Toast.makeText(activity, "Your request has been sent", Toast.LENGTH_LONG).show()
                                view?.findViewById<EditText>(R.id.settingsPermissionsRequestComment)?.setText("Request has been sent!")
                            } else {
                                Toast.makeText(activity, "Something went wrong! Try again!", Toast.LENGTH_LONG).show()
                            }
                        }
                        })

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
         * @return A new instance of fragment settingsVersionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            settingsVersionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}