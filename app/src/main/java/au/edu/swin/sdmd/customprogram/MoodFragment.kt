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
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

private const val KEY_YEAR = "au.edu.swin.sdmd.customprogram.year"
private const val KEY_MONTH = "au.edu.swin.sdmd.customprogram.month"

class MoodFragment : Fragment() {

    private lateinit var vPreviousButton : ImageButton
    private lateinit var vNextButton : ImageButton
    private lateinit var vMonthText : TextView
    private lateinit var vMoodChart: PieChart
    private lateinit var vMoodIcon : ImageView
    private lateinit var vMonthMoodText : TextView
    private lateinit var vNoEntry : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var chosenYear = Calendar.getInstance().get(Calendar.YEAR)
    private var chosenMonth = Calendar.getInstance().get(Calendar.MONTH)
    private val journalRepository = JournalRepository.get()
    private val monthName = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private val dataColorArray = mutableListOf(
        Color.argb(255, 119, 172, 223),
        Color.argb(255, 204, 135, 90),
        Color.argb(255, 125, 87, 221),
        Color.argb(255, 189, 225, 97),
        Color.argb(255, 198, 86, 170))


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Initialize views
        vPreviousButton = view.findViewById(R.id.previous_button)
        vNextButton = view.findViewById(R.id.next_button)
        vMonthText = view.findViewById(R.id.month_text)
        vMoodChart = view.findViewById(R.id.mood_chart)
        vMoodIcon = view.findViewById(R.id.mood_icon)
        vMonthMoodText = view.findViewById(R.id.month_mood_text)
        vNoEntry = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        if (savedInstanceState != null)
        {
            chosenYear = savedInstanceState.getInt(KEY_YEAR)
            chosenMonth = savedInstanceState.getInt(KEY_MONTH)
        }

        updateData()

        // Setting up the chart characteristic
        vMoodChart.legend.isEnabled = false
        vMoodChart.description.isEnabled = false
        vMoodChart.setNoDataText("")
        vMoodChart.holeRadius = 0f
        vMoodChart.transparentCircleRadius= 0f

        // Setup listeners
        vPreviousButton.setOnClickListener {
            previousMonth()
            updateData()
        }

        vNextButton.setOnClickListener {
            nextMonth()
            updateData()
        }

        vNewEntry.setOnClickListener {
            val intent = EntryInputActivity.newIntent(activity?.applicationContext, Journal(), false)
            startActivity(intent)
        }

        return view
    }

    /*
        This function will set the current month to the month before
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
        This function will set the current month to the month after
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

    private fun updateUI(journals: List<Journal>) {
        val monthText = "${monthName[chosenMonth]} $chosenYear"
        val moodFrequency = arrayOf(0,0,0,0,0)
        val entries : ArrayList<PieEntry> = ArrayList()
        val dataSet = PieDataSet(entries, "Mood")
        val pieData = PieData(dataSet)
        val maxIndex: Int

        vMonthText.text = monthText

        if (journals.isNotEmpty())
        {
            for (journal in journals)
            {
                moodFrequency[5 - journal.journalMood]++
            }


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

            dataSet.colors = dataColorArray
            pieData.setValueTextSize(12f)
            pieData.setValueTextColor(Color.WHITE)

            vMoodChart.data = pieData
            vNoEntry.visibility = View.INVISIBLE
            vMoodIcon.visibility = View.VISIBLE
            vMonthMoodText.visibility = View.VISIBLE

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
            vMonthMoodText.visibility = View.INVISIBLE
        }

        vMoodChart.invalidate()

    }


    private fun updateData() {
        journalRepository.getJournalsByMonth(chosenYear, chosenMonth).observe(
            viewLifecycleOwner,
            Observer { journals ->
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
        updateData()
        Log.d("AAA", "Onstart $chosenMonth")

    }


}