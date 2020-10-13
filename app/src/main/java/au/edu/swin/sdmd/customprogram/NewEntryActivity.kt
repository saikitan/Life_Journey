package au.edu.swin.sdmd.customprogram

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_entry_input.*
import java.text.DateFormat
import java.util.*

class NewEntryActivity : AppCompatActivity() {

    private lateinit var vDiscardButton: ImageButton
    private lateinit var vFinishButton: ImageButton
    private lateinit var vEntryDateButton : Button
    private lateinit var vEntryTimeButton : Button
    private lateinit var vEntryMoodButton : Button
    private lateinit var vEntryContentInput : EditText
    private val journalEntry = Journal()
    private val calendarInstance = Calendar.getInstance()
    private lateinit var formattedDate : String
    private lateinit var formattedTime: String

    var discardButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)

        vDiscardButton = findViewById(R.id.discard_button)
        vFinishButton = findViewById(R.id.finish_button)
        vEntryDateButton = findViewById(R.id.date_input)
        vEntryTimeButton = findViewById(R.id.time_input)
        vEntryMoodButton = findViewById(R.id.mood_input)
        vEntryContentInput = findViewById(R.id.content_input)

        updateDateTimeUI()
        updateMoodUI()

        vEntryDateButton.setOnClickListener {
            DatePickerDialog(this,
                {view, mYear, mMonth, mDay ->
                    calendarInstance.set(mYear, mMonth, mDay)
                    journalEntry.journalDate = calendarInstance.time
                    updateDateTimeUI()
                    },
                calendarInstance.get(Calendar.YEAR),
                calendarInstance.get(Calendar.MONTH),
                calendarInstance.get(Calendar.DAY_OF_MONTH)).show()
        }

        vEntryTimeButton.setOnClickListener {
            TimePickerDialog(this,
                {timePicker, hour, minute ->
                    calendarInstance.set(Calendar.HOUR_OF_DAY, hour)
                    calendarInstance.set(Calendar.MINUTE, minute)
                    journalEntry.journalDate = calendarInstance.time
                    updateDateTimeUI()
                },
                calendarInstance.get(Calendar.HOUR_OF_DAY),
                calendarInstance.get(Calendar.MINUTE),
                true).show()
        }

        vEntryMoodButton.setOnClickListener {
            val items = arrayOf(getString(R.string.very_good_mood),
                                getString(R.string.good_mood),
                                getString(R.string.neutral_mood),
                                getString(R.string.bad_mood),
                                getString(R.string.very_bad_mood),)

            var choice: Int = journalEntry.journalMood

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.mood))
                .setNeutralButton(resources.getString(R.string.cancel)){_,_ ->}
                .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                    // Respond to positive button press
                    journalEntry.journalMood = choice
                    updateMoodUI()
                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(items, 5 - choice) { _, which ->
                    choice = 5 - which
                }
                .show()
        }

        vFinishButton.setOnClickListener {
            journalEntry.journalData = vEntryContentInput.text.toString()
            JournalRepository.get().addJournal(journalEntry)
            finish()
        }

        vDiscardButton.setOnClickListener {
            showDiscardAlert()
        }

    }

    private fun updateMoodUI () {
        when (journalEntry.journalMood)
        {
            1 -> vEntryMoodButton.text = getString(R.string.very_bad_mood_wrap)
            2 -> vEntryMoodButton.text = getString(R.string.bad_mood)
            4 -> vEntryMoodButton.text = getString(R.string.good_mood)
            5 -> vEntryMoodButton.text = getString(R.string.very_good_mood_wrap)
            else -> vEntryMoodButton.text = getString(R.string.neutral_mood)
        }
    }

    private fun updateDateTimeUI() {
        formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(journalEntry.journalDate)
        formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(journalEntry.journalDate)
        vEntryDateButton.text = formattedDate
        vEntryTimeButton.text = formattedTime
    }

    private fun showDiscardAlert (){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.discard_title))
            .setMessage(resources.getString(R.string.discard_supporting_text))
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.discard)) { _, _ ->
                finish()
            }
            .show()
    }

    override fun onBackPressed() {
        showDiscardAlert()
    }

    companion object{
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, NewEntryActivity::class.java)
        }
    }
}