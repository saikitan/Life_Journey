package au.edu.swin.sdmd.customprogram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

private const val KEY_YEAR = "au.edu.swin.sdmd.customprogram.year"
private const val KEY_MONTH = "au.edu.swin.sdmd.customprogram.month"
private const val KEY_DAY = "au.edu.swin.sdmd.customprogram.day"

class CalendarFragment : Fragment() {

    private lateinit var vJournalList : RecyclerView
    private lateinit var vCalendar : CalendarView
    private lateinit var vNoEntry : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}
    private val calendarInstance = Calendar.getInstance()
    private var chosenDay: Int = calendarInstance.get(Calendar.DAY_OF_MONTH)
    private var chosenMonth: Int = calendarInstance.get(Calendar.MONTH)
    private var chosenYear: Int = calendarInstance.get(Calendar.YEAR)

    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProvider(this).get(JournalListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize View
        vJournalList = view.findViewById(R.id.entries_list)
        vCalendar = view.findViewById(R.id.calendar_view)
        vNoEntry = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        if (savedInstanceState != null)
        {
            chosenYear = savedInstanceState.getInt(KEY_YEAR)
            chosenMonth = savedInstanceState.getInt(KEY_MONTH)
            chosenDay = savedInstanceState.getInt(KEY_DAY)

            // Set the calender view to selected date
            calendarInstance.set(chosenYear, chosenMonth, chosenDay)
            vCalendar.date = calendarInstance.timeInMillis

            updateJournalsList()
        }

        // Initialize Recycler View
        vJournalList.layoutManager = LinearLayoutManager(context)
        vJournalList.adapter = adapter

        // Setup Listener
        vCalendar.setOnDateChangeListener { _, year, month, day ->
            chosenYear = year
            chosenMonth = month
            chosenDay = day
            updateJournalsList()
        }

        vNewEntry.setOnClickListener {
            val intent = EntryInputActivity.newIntent(activity?.applicationContext, Journal(), false)
            startActivity(intent)
        }

        return view
    }

    /*
        This function update the UI that shows the journal lists
        Parameter:  journals - A list contains journals
     */
    private fun updateUI(journals: List<Journal>) {
        adapter = TheAdapter(journals) {showDetails(it)}

        if (adapter.itemCount == 0)
        {
            vNoEntry.visibility = View.VISIBLE
        }
        else
        {
            vNoEntry.visibility = View.INVISIBLE
        }

        vJournalList.adapter = adapter
    }

    /*
        This function retrieves the journals of chosen date and update the UI
     */
    private fun updateJournalsList() {
        journalListViewModel.getAllJournalsByDate(chosenYear,chosenMonth, chosenDay).observe(
            viewLifecycleOwner,
            { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })
    }

    /*
        This function will start the another activity that show the details of the journal
        Parameter:  item - Journal to be shown
     */
    private fun showDetails(item : Journal)
    {
        val intent = EntryDetailsActivity.newIntent(activity, item.id.toString())
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        // Set the calender view to selected date
        calendarInstance.set(chosenYear, chosenMonth, chosenDay)
        vCalendar.date = calendarInstance.timeInMillis

        updateJournalsList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_YEAR, chosenYear)
        outState.putInt(KEY_MONTH, chosenMonth)
        outState.putInt(KEY_DAY, chosenDay)
    }



}