package au.edu.swin.sdmd.customprogram

import android.os.Bundle
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
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var journalRecyclerView : RecyclerView
    private lateinit var calendarView : CalendarView
    private lateinit var noEntryView : TextView
    private var adapter = TheAdapter(emptyList())
    private var chosenDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var chosenMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var chosenYear: Int = Calendar.getInstance().get(Calendar.YEAR)


    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProviders.of(this).get(JournalListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_calendar, container, false)
        journalRecyclerView = view.findViewById(R.id.entries_list)
        calendarView = view.findViewById(R.id.calendar_view)
        noEntryView = view.findViewById(R.id.no_entry)
        
        journalRecyclerView.layoutManager = LinearLayoutManager(context)
        journalRecyclerView.adapter = adapter
        
        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            chosenYear = year
            chosenMonth = month
            chosenDay = day
            updateJournalsList()
        }
        return view
    }

    private fun updateUI(journals: List<Journal>) {
        adapter = TheAdapter(journals)

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
        journalListViewModel.getAllJournalsByDate(chosenYear,chosenMonth, chosenDay).observe(
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

}