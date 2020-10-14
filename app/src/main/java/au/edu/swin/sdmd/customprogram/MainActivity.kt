package au.edu.swin.sdmd.customprogram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var vBottomNavigation : BottomNavigationView
    private lateinit var appBar : Toolbar
    private lateinit var vNewEntry : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        appBar = findViewById(R.id.app_bar)
        vNewEntry = findViewById(R.id.new_entry)

        setSupportActionBar(appBar)


        if (currentFragment == null)
        {
            val fragment = EntriesFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        vBottomNavigation = findViewById(R.id.bottom_navigation)

        vBottomNavigation.setOnNavigationItemSelectedListener {
            lateinit var fragment : Fragment

            when(it.itemId) {
                R.id.entries_view -> {
                    // Respond to navigation item 1 click
                    fragment = EntriesFragment()
                    Log.d("MainActivity", "Entries clicked")
                }
                R.id.calendar_view -> {
                    // Respond to navigation item 2 click
                    fragment = CalendarFragment()
                    Log.d("MainActivity", "Calendar clicked")
                }
                R.id.mood_view -> {
                    fragment = MoodFragment()
                    Log.d("MainActivity", "Mood clicked")
                }
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            true

        }

        vNewEntry.setOnClickListener {
            val intent = EntryInputActivity.newIntent(this, Journal(), false)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbar_action, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                Log.d("MainActivity", "Search clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}