package au.edu.swin.sdmd.customprogram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EntriesFragment : Fragment() {

    private lateinit var vJournalList: RecyclerView
    private lateinit var vNoEntry : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}
    private val journalRepository = JournalRepository.get()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_entries, container, false)

        // Initialize views
        vJournalList = view.findViewById(R.id.entries_list)
        vNoEntry = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        // Initialize RecyclerView
        vJournalList.layoutManager = LinearLayoutManager(context)
        vJournalList.adapter = adapter

        // Setup Listeners
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
        This function retrieves all the journals from the database and update the UI
     */
    private fun updateJournalsList() {
        journalRepository.getJournals().observe(
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
        updateJournalsList()
    }


}