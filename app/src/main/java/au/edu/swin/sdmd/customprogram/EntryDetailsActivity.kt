package au.edu.swin.sdmd.customprogram

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.DateFormat
import java.util.*

const val JOURNAL_KEY = "au.edu.swin.sdmd.customprogram.journaldetails"
private const val REQUEST_CODE = 0

class EntryDetailsActivity : AppCompatActivity() {

    private lateinit var vBackButton : ImageButton
    private lateinit var vEditButton : ImageButton
    private lateinit var vDeleteButton : ImageButton
    private lateinit var vEntryDate : TextView
    private lateinit var vEntryTime : TextView
    private lateinit var vEntryMoodText : TextView
    private lateinit var vEntryMoodIcon : ImageView
    private lateinit var vEntryContent : TextView
    private lateinit var vEntryImage : ImageView
    private lateinit var journalImage : File
    private lateinit var journal: Journal

    private val journalDetailsViewModel: JournalDetailsViewModel by lazy {
        ViewModelProvider(this).get(JournalDetailsViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)

        // Initialize view
        vBackButton = findViewById(R.id.back_button)
        vEditButton = findViewById(R.id.edit_button)
        vDeleteButton = findViewById(R.id.delete_button)
        vEntryDate = findViewById(R.id.entry_date_text)
        vEntryTime = findViewById(R.id.entry_time_text)
        vEntryMoodText = findViewById(R.id.entry_mood_text)
        vEntryMoodIcon = findViewById(R.id.entry_mood_icon)
        vEntryContent = findViewById(R.id.entry_content)
        vEntryImage = findViewById(R.id.journal_image)
        vEntryContent.movementMethod = ScrollingMovementMethod()

        updateJournal()

        // Setup listeners
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

        vEntryImage.setOnClickListener {
            val i = PhotoDetailsActivity.newIntent(this, journal, false)
            startActivityForResult(i, VIEW_PHOTO)
        }

    }

    /*
        This function will update all the UI elements for the Journal (Date, Time, Mood, Content, Image)
     */
    private fun updateUI() {
        // Update Date and Time
        vEntryDate.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(journal.journalDate)
        vEntryTime.text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(journal.journalDate)

        // Update Mood Icon and Text
        when (journal.journalMood)
        {
            1 -> {
                vEntryMoodText.text = getString(R.string.very_bad_mood)
                vEntryMoodIcon.setImageResource(R.drawable.ic_very_bad_mood_24)
            }
            2 -> {
                vEntryMoodText.text = getString(R.string.bad_mood)
                vEntryMoodIcon.setImageResource(R.drawable.ic_bad_mood_24)
            }
            4 -> {
                vEntryMoodText.text = getString(R.string.good_mood)
                vEntryMoodIcon.setImageResource(R.drawable.ic_good_mood_24)
            }
            5 -> {
                vEntryMoodText.text = getString(R.string.very_good_mood)
                vEntryMoodIcon.setImageResource(R.drawable.ic_very_good_mood_24)
            }
            else -> {
                vEntryMoodText.text = getString(R.string.neutral_mood)
                vEntryMoodIcon.setImageResource(R.drawable.ic_neutral_mood_24)
            }
        }

        when (journal.journalMood) {
            1,2 -> vEntryMoodText.setTextColor(Color.argb(255, 139, 0, 0))
            4,5 -> vEntryMoodText.setTextColor(Color.argb(255, 0, 100, 0))
            else -> vEntryMoodText.setTextColor(Color.argb(255, 255, 165, 0))
        }

        // Update Journal Content
        vEntryContent.text = journal.journalData

        if (journalImage.exists()) {
            val bitmap = getScaledBitmap(journalImage.path, this)
            vEntryImage.setImageBitmap(bitmap)
        }
        else {
            vEntryImage.setImageDrawable(null)
        }
    }

    /*
        This function will retrieve the latest info about the journal
     */
    private fun updateJournal()
    {
        journalDetailsViewModel.getJournal(UUID.fromString(intent.getStringExtra(JOURNAL_KEY))).observe(
            this,
            { journalFound ->
                journalFound?.let {
                    journal = journalFound
                    journalImage = journalDetailsViewModel.getPhotoFile(journal)
                    updateUI()
                }
            }
        )
    }

    /*
        This function will create a delete confirmation dialog and perform action
        according to the user response.
     */
    private fun showDeleteAlert (){
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.delete_title))
                .setMessage(resources.getString(R.string.delete_supporting_text))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                    journalDetailsViewModel.deleteJournal(journal)
                    journalImage.delete()
                    Toast.makeText(this, getString(R.string.journal_deleted), Toast.LENGTH_SHORT).show()
                    finish()
                }
                .show()

    }
    

    override fun onStart() {
        super.onStart()
        updateJournal()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_CODE) {
            val journalReturn = data?.getParcelableExtra<Journal>(JOURNAL_KEY)

            if (journalReturn != null) // Check whether journal is being deleted
            {
                updateJournal()
            }
            else {
                finish()
            }
        }
    }

    companion object{
        fun newIntent(fragmentActivity: FragmentActivity?, journalId: String): Intent {
            return Intent(fragmentActivity, EntryDetailsActivity::class.java).apply {
                putExtra(JOURNAL_KEY, journalId)
            }
        }
    }
}