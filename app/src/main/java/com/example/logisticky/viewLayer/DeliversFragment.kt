package com.example.logisticky.viewLayer

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logisticky.*
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
    var testList = ArrayList<DeliveryItem>()
    var displayList = ArrayList<DeliveryItem>()
    lateinit var recyclerView: RecyclerView

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
        return inflater.inflate(R.layout.fragment_delivers, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeliversFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeliversFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val dataFromApi2 = async {
                testList.clear()
                token?.let { DeliverysHandler.getAllDeliverys(it) }

            }.await()


            println("Debug: Make Delivery code: $dataFromApi2")
            dataFromApi2?.deliverysList?.forEach{


//                val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
//
//                val offsetDateTime: OffsetDateTime =
//                    OffsetDateTime.parse (it.date, timeFormatter)
//
//                val date = Date.from (Instant.from(offsetDateTime))
//
//
//                //tu dobrze pokazuje czas
//                val cal = Calendar.getInstance()
//                cal.time = date
//                val hours = cal.get(Calendar.HOUR)
//                val mins = cal.get(Calendar.MINUTE)



                val createdDate = getDateFromIso8601String(it.date)
                val createdDateString = "Created: ${it.date.take(10)}, ${String.format("%02d:%02d", createdDate.get(Calendar.HOUR_OF_DAY), createdDate.get(Calendar.MINUTE))} |"
                val pickupDate = getDateFromIso8601String(it.pickupDate)
                val pickupDateString = "Due date: ${it.pickupDate.take(10)}, ${String.format("%02d:%02d", pickupDate.get(Calendar.HOUR_OF_DAY), pickupDate.get(Calendar.MINUTE))}"

                testList.add(DeliveryItem(it.deliveryId, it.state, createdDateString, pickupDateString))

                println(it.date)
                updateUI()

            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search, menu)
        val menuItem = menu!!.findItem(R.id.searchProducts)

        if(menuItem != null){

            val searchView = menuItem.actionView as SearchView
            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search Your Product..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        testList.forEach {

                            if (it.deliveryId.toString().toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                        }


                        recyclerView.adapter!!.notifyDataSetChanged()
                    } else {

                        displayList.clear()
                        displayList.addAll(testList)

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



    private fun updateUI(){
        activity?.runOnUiThread(object: Runnable {
            override fun run() {

                displayList = testList.reversed() as ArrayList<DeliveryItem>

                recyclerView = view!!.findViewById(R.id.delivers_recycleView)
                recyclerView?.adapter = DeliverysAdapter(displayList)
                recyclerView?.layoutManager = LinearLayoutManager(activity)
                recyclerView?.setHasFixedSize(true)

            }

        })

    }
    fun getDateFromIso8601String (date:String):Calendar{
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        val offsetDateTime: OffsetDateTime =
            OffsetDateTime.parse (date, timeFormatter)

        val date = Date.from (Instant.from(offsetDateTime))


        //tu dobrze pokazuje czas
        val cal = Calendar.getInstance()
        cal.time = date
//    val hours = cal.get(Calendar.HOUR)
//    val mins = cal.get(Calendar.MINUTE)
        return cal
    }
}