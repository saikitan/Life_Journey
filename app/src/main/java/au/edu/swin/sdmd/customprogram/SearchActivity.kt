package au.edu.swin.sdmd.customprogram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val KEY_QUERY = "au.edu.swin.sdmd.customprogram.searchquery"

class SearchActivity : AppCompatActivity() {

    private lateinit var vSearch : SearchView
    private lateinit var vJournalList : RecyclerView
    private lateinit var vNoEntry : TextView
    private var searchQuery = ""
    private var adapter = TheAdapter(emptyList()) {showDetails(it)}

    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProvider(this).get(JournalListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null)
        {
            searchQuery = savedInstanceState.getString(KEY_QUERY) ?: ""
            updateJournalsList(searchQuery)
        }

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
                    searchQuery = query
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
        journalListViewModel.getAllJournalsBySearch(searchString).observe(
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, searchQuery)
    }


}