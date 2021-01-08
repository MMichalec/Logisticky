package com.example.logisticky.viewLayer

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeliversFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeliversFragment : Fragment() {

   private lateinit var recyclerView: RecyclerView
   private lateinit var navController: NavController
   private var deliversList = ArrayList<DeliveryItem>()
   private var displayList = ArrayList<DeliveryItem>()
   private var token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        //fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //Checking what fragment called (first on backstack). If the Make Delivery info called this fragment it clears backstack and overrides back button action
        val callersName = getCallerFragment()
        //TODO callers name is changing. Need to find out what this name means
        if (callersName?.take(1) == "4" ){
            val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                navController.navigate(R.id.action_deliversFragment_to_mainMenuFragment)
                clearBackStack()
            }
            this.requireActivity().onBackPressedDispatcher.addCallback(this, callback);
        }

        token = this.activity?.let { TokenManager.loadData(it) }

    }
// Overriding onDestroyView to make cartFAB (Floating Action Bar) visible
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.visibility = View.VISIBLE
        println("Debug: destruction")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_delivers, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        CoroutineScope(Dispatchers.IO).launch {
            val deliversDataFromApi = async {
                deliversList.clear()
                token?.let { DeliverysHandler.getAllDeliverys(it) }
            }.await()



            deliversDataFromApi?.deliverysList?.forEach {

                //Date shenanigans
                val createdDate = getDateFromIso8601String(it.date)
                val createdDateString = "Created: ${it.date.take(10)}, ${
                    String.format("%02d:%02d", createdDate.get(Calendar.HOUR_OF_DAY), createdDate.get(Calendar.MINUTE))} |"
                val pickupDate = getDateFromIso8601String(it.pickupDate)
                val pickupDateString = "Due date: ${it.pickupDate.take(10)}, ${
                    String.format("%02d:%02d", pickupDate.get(Calendar.HOUR_OF_DAY), pickupDate.get(Calendar.MINUTE))}"

                //Creatinf deliversList items to display in recycler view
                deliversList.add(
                    DeliveryItem(
                        it.deliveryId,
                        it.state,
                        createdDateString,
                        pickupDateString
                    )
                )
            }
            deliversList.reverse()
            updateUI()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)

        val menuItem = menu.findItem(R.id.search)

        if (menuItem != null) {

            val searchView = menuItem.actionView as SearchView
            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search delivery by ID..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        deliversList.forEach {

                            if (it.deliveryId.toString().toLowerCase(Locale.getDefault()).contains(
                                    search
                                )
                            ) {
                                displayList.add(it)
                            }
                        }


                        recyclerView.adapter!!.notifyDataSetChanged()
                    } else {

                        displayList.clear()
                        displayList.addAll(deliversList)

                        recyclerView.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }

            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    private fun updateUI() {
        activity?.runOnUiThread(object : Runnable {
            override fun run() {
                activity?.findViewById<FloatingActionButton>(R.id.cartFab)?.visibility = View.GONE
                view?.findViewById<ProgressBar>(R.id.deliversLoader)?.visibility = View.GONE

                displayList.clear()
                displayList.addAll(deliversList)

                recyclerView = view!!.findViewById(R.id.delivers_recycleView)
                recyclerView.adapter = DeliverysAdapter(displayList)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)

            }

        })

    }

    private fun getDateFromIso8601String(date: String): Calendar {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        val offsetDateTime: OffsetDateTime =
            OffsetDateTime.parse(date, timeFormatter)

        val date = Date.from(Instant.from(offsetDateTime))


        //tu dobrze pokazuje czas
        val cal = Calendar.getInstance()
        cal.time = date

        return cal
    }

    private fun getCallerFragment(): String? {
        val fm: FragmentManager? = fragmentManager
        val count = requireFragmentManager().backStackEntryCount
        if (fm != null) {
            return fm.getBackStackEntryAt(count - 1).name
        } else return null
    }

    private fun clearBackStack() {
        val manager: FragmentManager? = getFragmentManager()
        if (manager != null) {
            if (manager.backStackEntryCount > 0) {
                val first = manager.getBackStackEntryAt(0)
                manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}
