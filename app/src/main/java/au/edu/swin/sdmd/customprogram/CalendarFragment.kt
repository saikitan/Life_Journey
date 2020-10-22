package au.edu.swin.sdmd.customprogram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_entry_details.*
import java.util.*

private const val KEY_YEAR = "au.edu.swin.sdmd.customprogram.year"
private const val KEY_MONTH = "au.edu.swin.sdmd.customprogram.month"
private const val KEY_DAY = "au.edu.swin.sdmd.customprogram.day"

class CalendarFragment : Fragment() {

    private lateinit var journalRecyclerView : RecyclerView
    private lateinit var calendarView : CalendarView
    private lateinit var noEntryView : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}
    private val calendarInstance = Calendar.getInstance()
    private var chosenDay: Int = calendarInstance.get(Calendar.DAY_OF_MONTH)
    private var chosenMonth: Int = calendarInstance.get(Calendar.MONTH)
    private var chosenYear: Int = calendarInstance.get(Calendar.YEAR)
    private val journalRepository = JournalRepository.get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_calendar, container, false)
        journalRecyclerView = view.findViewById(R.id.entries_list)
        calendarView = view.findViewById(R.id.calendar_view)
        noEntryView = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        if (savedInstanceState != null)
        {
            chosenYear = savedInstanceState.getInt(KEY_YEAR)
            chosenMonth = savedInstanceState.getInt(KEY_MONTH)
            chosenDay = savedInstanceState.getInt(KEY_DAY)
            calendarInstance.set(chosenYear, chosenMonth, chosenDay)

            calendarView.date = calendarInstance.timeInMillis

            updateJournalsList()
        }

        journalRecyclerView.layoutManager = LinearLayoutManager(context)
        journalRecyclerView.adapter = adapter
        
        calendarView.setOnDateChangeListener { _, year, month, day ->
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

    private fun updateUI(journals: List<Journal>) {
        adapter = TheAdapter(journals) {showDetails(it)}

        if (adapter.itemCount == 0)
        {
            noEntryView.visibility = View.VISIBLE
        }
        else
        {
            noEntryView.visibility = View.INVISIBLE
        }

        journalRecyclerView.adapter = adapter
    }

    private fun updateJournalsList() {
        journalRepository.getJournalsByDate(chosenYear,chosenMonth, chosenDay).observe(
            viewLifecycleOwner,
            Observer { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })
    }

    override fun onStart() {
        super.onStart()
        updateJournalsList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_YEAR, chosenYear)
        outState.putInt(KEY_MONTH, chosenMonth)
        outState.putInt(KEY_DAY, chosenDay)
    }

    private fun showDetails(item : Journal)
    {
        val intent = EntryDetailsActivity.newIntent(activity, item.id.toString())
        startActivity(intent)
    }

}