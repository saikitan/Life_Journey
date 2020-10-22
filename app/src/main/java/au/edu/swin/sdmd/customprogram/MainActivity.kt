package au.edu.swin.sdmd.customprogram

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var vBottomNavigation : BottomNavigationView
    private lateinit var appBar : Toolbar
    private lateinit var entriesFragment: EntriesFragment
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var moodFragment: MoodFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Setup AppBar
        appBar = findViewById(R.id.app_bar)
        setSupportActionBar(appBar)

        // Initialize Fragment
        entriesFragment = EntriesFragment()
        calendarFragment = CalendarFragment()
        moodFragment = MoodFragment()


        if (currentFragment == null)
        {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, entriesFragment)
                .commit()
        }

        // Setup Bottom Navigation
        vBottomNavigation = findViewById(R.id.bottom_navigation)

        vBottomNavigation.setOnNavigationItemSelectedListener {
            lateinit var fragment : Fragment

            when(it.itemId) {
                R.id.entries_view -> {
                    fragment = entriesFragment
                }
                R.id.calendar_view -> {
                    fragment = calendarFragment
                }
                R.id.mood_view -> {
                    fragment = moodFragment
                }
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            true

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
                val i = Intent (this, SearchActivity::class.java)
                startActivity(i)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}