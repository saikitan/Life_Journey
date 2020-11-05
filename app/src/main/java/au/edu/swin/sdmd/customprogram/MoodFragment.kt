package au.edu.swin.sdmd.customprogram

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

private const val KEY_YEAR = "au.edu.swin.sdmd.customprogram.moodfragment.year"
private const val KEY_MONTH = "au.edu.swin.sdmd.customprogram.moodfagment.month"

class MoodFragment : Fragment() {

    private lateinit var vPreviousButton : ImageButton
    private lateinit var vNextButton : ImageButton
    private lateinit var vMonth : TextView
    private lateinit var vMoodChart: PieChart
    private lateinit var vMoodIcon : ImageView
    private lateinit var vMonthMood : TextView
    private lateinit var vNoEntry : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var chosenYear = Calendar.getInstance().get(Calendar.YEAR)
    private var chosenMonth = Calendar.getInstance().get(Calendar.MONTH)
    private val monthName = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private val dataColorArray = mutableListOf(
        Color.argb(255, 119, 172, 223),
        Color.argb(255, 204, 135, 90),
        Color.argb(255, 125, 87, 221),
        Color.argb(255, 189, 225, 97),
        Color.argb(255, 198, 86, 170))

    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProvider(this).get(JournalListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Initialize views
        vPreviousButton = view.findViewById(R.id.previous_button)
        vNextButton = view.findViewById(R.id.next_button)
        vMonth = view.findViewById(R.id.month_text)
        vMoodChart = view.findViewById(R.id.mood_chart)
        vMoodIcon = view.findViewById(R.id.mood_icon)
        vMonthMood = view.findViewById(R.id.month_mood_text)
        vNoEntry = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        if (savedInstanceState != null)
        {
            chosenYear = savedInstanceState.getInt(KEY_YEAR)
            chosenMonth = savedInstanceState.getInt(KEY_MONTH)
        }

        updateJournalsList()

        // Setting up the chart characteristic
        vMoodChart.legend.isEnabled = false
        vMoodChart.description.isEnabled = false
        vMoodChart.setNoDataText("")
        vMoodChart.holeRadius = 0f
        vMoodChart.transparentCircleRadius= 0f

        // Setup listeners
        vPreviousButton.setOnClickListener {
            previousMonth()
            updateJournalsList()
        }

        vNextButton.setOnClickListener {
            nextMonth()
            updateJournalsList()
        }

        vNewEntry.setOnClickListener {
            val intent = EntryInputActivity.newIntent(activity?.applicationContext, Journal(), false)
            startActivity(intent)
        }

        return view
    }

    /*
        This function will set the chosen month to the month before
     */
    private fun previousMonth()
    {
        if (chosenMonth != 0)
        {
            chosenMonth--
        }
        else
        {
            chosenMonth = 11
            chosenYear--
        }
    }

    /*
        This function will set the chosen month to the month after
     */
    private fun nextMonth()
    {
        if (chosenMonth != 11)
        {
            chosenMonth++
        }
        else
        {
            chosenMonth = 0
            chosenYear++
        }
    }

    /*
        This function will update all the UI elements
        Parameter:  journals - List of journals to be put in the mood chart
     */
    private fun updateUI(journals: List<Journal>) {
        val monthText = "${monthName[chosenMonth]} $chosenYear"
        val moodFrequency = arrayOf(0,0,0,0,0)
        val entries : ArrayList<PieEntry> = ArrayList()
        val dataSet = PieDataSet(entries, "Mood")
        val pieData = PieData(dataSet)
        val maxIndex: Int

        vMonth.text = monthText

        if (journals.isNotEmpty())
        {
            // Calculate the number of entries for each mood
            for (journal in journals)
            {
                moodFrequency[5 - journal.journalMood]++
            }

            // Update the data of the chart
            if (moodFrequency[0] != 0)
            {
                entries.add(PieEntry(moodFrequency[0].toFloat(), getString(R.string.very_good_mood)))
            }

            if (moodFrequency[1] != 0)
            {
                entries.add(PieEntry(moodFrequency[1].toFloat(), getString(R.string.good_mood)))
            }

            if (moodFrequency[2] != 0)
            {
                entries.add(PieEntry(moodFrequency[2].toFloat(), getString(R.string.neutral_mood)))
            }

            if (moodFrequency[3] != 0)
            {
                entries.add(PieEntry(moodFrequency[3].toFloat(), getString(R.string.bad_mood)))
            }

            if (moodFrequency[4] != 0)
            {
                entries.add(PieEntry(moodFrequency[4].toFloat(), getString(R.string.very_bad_mood)))
            }

            // Setting up the chart data characteristics
            dataSet.colors = dataColorArray
            pieData.setValueTextSize(12f)
            pieData.setValueTextColor(Color.WHITE)

            vMoodChart.data = pieData
            vNoEntry.visibility = View.INVISIBLE
            vMoodIcon.visibility = View.VISIBLE
            vMonthMood.visibility = View.VISIBLE

            // Find which mood has the highest frequency
            maxIndex = moodFrequency.indexOf(moodFrequency.maxOrNull())

            when (maxIndex)
            {
                0 -> vMoodIcon.setImageResource(R.drawable.ic_very_good_mood_24)
                1-> vMoodIcon.setImageResource(R.drawable.ic_good_mood_24)
                3 -> vMoodIcon.setImageResource(R.drawable.ic_bad_mood_24)
                4 -> vMoodIcon.setImageResource(R.drawable.ic_very_bad_mood_24)
                else -> vMoodIcon.setImageResource(R.drawable.ic_neutral_mood_24)
            }

            when (maxIndex)
            {
                0,1 -> vMoodIcon.setColorFilter(Color.argb(255, 0, 100, 0))
                3,4 -> vMoodIcon.setColorFilter((Color.argb(255, 139, 0, 0)))
                else -> vMoodIcon.setColorFilter(Color.argb(255, 255, 165, 0))
            }
        }
        else
        {
            vMoodChart.data = null
            vNoEntry.visibility = View.VISIBLE
            vMoodIcon.visibility = View.INVISIBLE
            vMonthMood.visibility = View.INVISIBLE
        }

        // Update the chart and show it in the view
        vMoodChart.invalidate()

    }

    /*
        This function will retrieve the list of journals according to the month selected
        and update the UI
     */
    private fun updateJournalsList() {
        journalListViewModel.getAllJournalsByMonth(chosenYear, chosenMonth).observe(
            viewLifecycleOwner,
            { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_YEAR, chosenYear)
        outState.putInt(KEY_MONTH, chosenMonth)
    }

    override fun onStart() {
        super.onStart()
        updateJournalsList()
        Log.d("MoodFragment", "onStart, Month: ${chosenMonth + 1}")

    }


}