package au.edu.swin.sdmd.customprogram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EntriesFragment : Fragment() {

    private lateinit var journalRecyclerView: RecyclerView
    private lateinit var noEntryView : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}
    private val journalRepository = JournalRepository.get()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_entries, container, false)
        journalRecyclerView = view.findViewById(R.id.entries_list)
        noEntryView = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        journalRecyclerView.layoutManager = LinearLayoutManager(context)
        journalRecyclerView.adapter = adapter

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


    override fun onStart() {
        super.onStart()
        journalRepository.getJournals().observe(
            viewLifecycleOwner,
            { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })

    }

    private fun showDetails(item : Journal)
    {
        val intent = EntryDetailsActivity.newIntent(activity, item.id.toString())
        startActivity(intent)
    }
}