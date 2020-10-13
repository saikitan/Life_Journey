package au.edu.swin.sdmd.customprogram

import android.graphics.Color
import android.graphics.Color.argb
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

class TheAdapter(private val data: List<Journal>) : RecyclerView.Adapter<TheAdapter.TheHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_raw, parent, false) as View
        return TheHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TheHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class TheHolder(private val v: View): RecyclerView.ViewHolder(v) {
        private lateinit var journal: Journal

        private var vEntryMoodIcon : ImageView = v.findViewById(R.id.entry_mood_icon)
        private var vEntryMoodText : TextView = v.findViewById(R.id.entry_mood)
        private var vEntryDate : TextView = v.findViewById(R.id.entry_date)
        private var vEntryTime : TextView = v.findViewById(R.id.entry_time)
        private var vEntryOverview : TextView = v.findViewById(R.id.entry_overview)

        fun bind(item: Journal) {
            this.journal = item
            var moodText: String

            when (item.journalMood)
            {
                1 -> {
                    moodText = "VERY BAD"
                    vEntryMoodIcon.setImageDrawable(getDrawable(v.context, R.drawable.ic_very_bad_mood_24))
                    }
                2 -> {
                    moodText = "BAD"
                    vEntryMoodIcon.setImageDrawable(getDrawable(v.context, R.drawable.ic_bad_mood_24))
                }
                4 -> {
                    moodText = "GOOD"
                    vEntryMoodIcon.setImageDrawable(getDrawable(v.context, R.drawable.ic_good_mood_24))
                }
                5 -> {
                    moodText = "VERY GOOD"
                    vEntryMoodIcon.setImageDrawable(getDrawable(v.context, R.drawable.ic_very_good_mood_24))
                }
                else -> {
                    moodText = "NEUTRAL"
                    vEntryMoodIcon.setImageDrawable(getDrawable(v.context, R.drawable.ic_neutral_mood_24))
                }
            }

            when (item.journalMood) {
                1,2 -> vEntryMoodText.setTextColor(argb(255, 139, 0,0))
                4,5 -> vEntryMoodText.setTextColor(argb(255, 0, 100,0))
                else -> vEntryMoodText.setTextColor(argb(255, 255, 165,0))
            }

            vEntryMoodText.text = moodText

            vEntryDate.text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(item.journalDate)
            vEntryTime.text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(item.journalDate)

            vEntryOverview.text = item.journalData


        }


    }
}
