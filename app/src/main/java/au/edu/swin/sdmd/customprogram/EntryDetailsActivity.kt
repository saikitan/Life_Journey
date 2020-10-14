package au.edu.swin.sdmd.customprogram

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.method.ScrollingMovementMethod
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DateFormat
import java.util.*

const val JOURNAL_KEY = "au.edu.swin.sdmd.customprogram.journaldetails"
private const val REQUEST_CODE = 0

class EntryDetailsActivity : AppCompatActivity() {

    private lateinit var vBackButton : ImageButton
    private lateinit var vEditButton : ImageButton
    private lateinit var vDeleteButton : ImageButton
    private lateinit var vEntryDateText : TextView
    private lateinit var vEntryTimeText : TextView
    private lateinit var vEntryMoodText : TextView
    private lateinit var vEntryMoodIcon : ImageView
    private lateinit var vEntryContentText : TextView
    private lateinit var journal: Journal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)

        journal = intent.getParcelableExtra(JOURNAL_KEY)

        vBackButton = findViewById(R.id.back_button)
        vEditButton = findViewById(R.id.edit_button)
        vDeleteButton = findViewById(R.id.delete_button)
        vEntryDateText = findViewById(R.id.entry_date_text)
        vEntryTimeText = findViewById(R.id.entry_time_text)
        vEntryMoodText = findViewById(R.id.entry_mood_text)
        vEntryMoodIcon = findViewById(R.id.entry_mood_icon)
        vEntryContentText = findViewById(R.id.entry_content)

        updateUI()

        vEntryContentText.movementMethod = ScrollingMovementMethod()

        vBackButton.setOnClickListener {
            finish()
        }

        vEditButton.setOnClickListener {
            val intent = EntryInputActivity.newIntent(this, journal, true)
            startActivityForResult(intent, REQUEST_CODE)
        }

        vDeleteButton.setOnClickListener {
            showDeleteAlert()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_CODE) {
            val journalReturn = data?.getParcelableExtra<Journal>(JOURNAL_KEY)

            if (journalReturn != null)
            {
                journal = journalReturn
            }
            else {
                finish()
            }

            updateUI()
        }

    }

    private fun updateUI() {
        vEntryDateText.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(journal.journalDate)
        vEntryTimeText.text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(journal.journalDate)

        when (journal.journalMood)
        {
            1 -> {
                vEntryMoodText.text = getString(R.string.very_bad_mood)
                vEntryMoodIcon.setImageDrawable(getDrawable(R.drawable.ic_very_bad_mood_24))
            }
            2 -> {
                vEntryMoodText.text = getString(R.string.bad_mood)
                vEntryMoodIcon.setImageDrawable(getDrawable(R.drawable.ic_bad_mood_24))
            }
            4 -> {
                vEntryMoodText.text = getString(R.string.good_mood)
                vEntryMoodIcon.setImageDrawable(getDrawable(R.drawable.ic_good_mood_24))
            }
            5 -> {
                vEntryMoodText.text = getString(R.string.very_good_mood)
                vEntryMoodIcon.setImageDrawable(getDrawable(R.drawable.ic_very_good_mood_24))
            }
            else -> {
                vEntryMoodText.text = getString(R.string.neutral_mood)
                vEntryMoodIcon.setImageDrawable(getDrawable(R.drawable.ic_neutral_mood_24))
            }
        }

        when (journal.journalMood) {
            1,2 -> vEntryMoodText.setTextColor(Color.argb(255, 139, 0, 0))
            4,5 -> vEntryMoodText.setTextColor(Color.argb(255, 0, 100, 0))
            else -> vEntryMoodText.setTextColor(Color.argb(255, 255, 165, 0))
        }

        vEntryContentText.text = journal.journalData
    }


    private fun showDeleteAlert (){
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.delete_title))
                .setMessage(resources.getString(R.string.delete_supporting_text))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                    JournalRepository.get().deleteJournal(journal)
                    finish()
                }
                .show()

    }

    companion object{
        fun newIntent(fragmentActivity: FragmentActivity?, journal: Parcelable): Intent {
            return Intent(fragmentActivity, EntryDetailsActivity::class.java).apply {
                putExtra(JOURNAL_KEY, journal)
            }
        }
    }
}