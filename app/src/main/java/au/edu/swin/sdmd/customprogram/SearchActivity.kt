package au.edu.swin.sdmd.customprogram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var vSearch : SearchView
    private lateinit var vJournalList : RecyclerView
    private lateinit var vNoEntry : TextView
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}
    private val journalRepository = JournalRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        vSearch = findViewById(R.id.search)
        vJournalList = findViewById(R.id.entries_list)
        vNoEntry = findViewById(R.id.no_entry)

        // Initialize RecyclerView
        vJournalList.layoutManager = LinearLayoutManager(this)
        vJournalList.adapter = adapter

        // Setup listener
        vSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            // Listener that check whether the user click submit
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                {
                    updateJournalsList(query)
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

    /*
        This function will the UI that show the list of journals
        Parameter:  journals - List of journals
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
        This function will retrieve the journals that meet the search query.
        Parameter:  searchString - Query that user entered for search
     */
    fun updateJournalsList(searchString : String) {
        journalRepository.getJournalsBySearch(searchString).observe(
            this,
            { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })

    }

    /*
        This function will open the details activity.
        Parameter:  item - Journal to be shown
     */
    private fun showDetails(item : Journal)
    {
        val intent = EntryDetailsActivity.newIntent(this, item.id.toString())
        startActivity(intent)
    }




}