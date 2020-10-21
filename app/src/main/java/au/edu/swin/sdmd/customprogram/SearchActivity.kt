package au.edu.swin.sdmd.customprogram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var vSearch : SearchView
    private lateinit var vJournalList : RecyclerView
    private lateinit var vNoEntry : TextView
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}

    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProviders.of(this).get(JournalListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        vSearch = findViewById(R.id.search)
        vJournalList = findViewById(R.id.entries_list)
        vNoEntry = findViewById(R.id.no_entry)

        vJournalList.layoutManager = LinearLayoutManager(this)
        vJournalList.adapter = adapter

        vSearch.setOnCloseListener {
            Log.d("AAAA", "Close clicked")
            true
        }

        vSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                {
                    updateJournals(query)
                }
                else
                {
                    updateUI(emptyList())
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    private fun updateUI(journals: List<Journal>) {
        adapter = TheAdapter(journals) {showDetails(it)}

        Log.d("AAA", adapter.itemCount.toString())

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


    fun updateJournals(searchString : String) {
        journalListViewModel.getAllJournalBySearch(searchString).observe(
            this,
            { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })

    }

    private fun showDetails(item : Journal)
    {
        val intent = EntryDetailsActivity.newIntent(this, item.id.toString())
        startActivity(intent)
    }




}