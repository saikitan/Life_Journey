package au.edu.swin.sdmd.customprogram

import android.annotation.SuppressLint
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
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList


class MoodFragment : Fragment() {

    private lateinit var vPreviousButton : ImageButton
    private lateinit var vNextButton : ImageButton
    private lateinit var vMonthText : TextView
    private lateinit var vMoodChart: PieChart
    private lateinit var vMoodIcon : ImageView
    private lateinit var vMonthMoodText : TextView
    private lateinit var vNoEntry : TextView
    private lateinit var vNewEntry : FloatingActionButton
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    private val monthName = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private val dataColorArray = mutableListOf(Color.argb(255, 202, 77, 63), Color.argb(255, 95, 151, 215), Color.argb(255, 229, 194, 50), Color.argb(255, 121, 203, 118), Color.RED)

    private val journalListViewModel: JournalListViewModel by lazy {
        ViewModelProviders.of(this).get(JournalListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        vPreviousButton = view.findViewById(R.id.previous_button)
        vNextButton = view.findViewById(R.id.next_button)
        vMonthText = view.findViewById(R.id.month_text)
        vMoodChart = view.findViewById(R.id.mood_chart)
        vMoodIcon = view.findViewById(R.id.mood_icon)
        vMonthMoodText = view.findViewById(R.id.month_mood_text)
        vNoEntry = view.findViewById(R.id.no_entry)
        vNewEntry = view.findViewById(R.id.new_entry)

        updateData()

        vMoodChart.legend.isEnabled = false
        vMoodChart.description.isEnabled = false

        //vMoodChart.setUsePercentValues(true)
        vMoodChart.setNoDataText("")
        vMoodChart.holeRadius = 0f
        vMoodChart.transparentCircleRadius= 0f

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

    private fun previousMonth()
    {
        if (currentMonth != 0)
        {
            currentMonth--
        }
        else
        {
            currentMonth = 11
            currentYear--
        }
    }

    private fun nextMonth()
    {
        if (currentMonth != 11)
        {
            currentMonth++
        }
        else
        {
            currentMonth = 0
            currentYear++
        }
    }

    private fun updateUI(journals: List<Journal>) {
        val monthText = "${monthName[currentMonth]} $currentYear"
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
        journalListViewModel.getAllJournalsByMonth(currentYear, currentMonth).observe(
            viewLifecycleOwner,
            Observer { journals ->
                journals?.let {
                    updateUI(journals)
                }
            })
    }

    override fun onStart() {
        super.onStart()
        updateData()
        Log.d("AAA", "Onstart")
    }


}