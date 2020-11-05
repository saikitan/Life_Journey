package au.edu.swin.sdmd.customprogram

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.DateFormat
import java.util.*

const val EDIT_KEY = "au.edu.swin.sdmd.customprogram.editjournal"
const val VIEW_PHOTO = 0
private const val REQUEST_PHOTO = 1

class EntryInputActivity : AppCompatActivity() {

    private lateinit var vDiscardButton: ImageButton
    private lateinit var vFinishButton: ImageButton
    private lateinit var vDeleteButton: ImageButton
    private lateinit var vEntryDateButton : Button
    private lateinit var vEntryTimeButton : Button
    private lateinit var vEntryMoodButton : Button
    private lateinit var vEntryContent : EditText
    private lateinit var vEntryImage: ImageView
    private lateinit var vCameraButton: ImageButton
    private lateinit var journal : Journal
    private lateinit var journalImage : File
    private lateinit var formattedDate : String
    private lateinit var formattedTime: String
    private lateinit var photoUri : Uri
    private var editJournal = false                         // Use to store whether it is in journal edit mode
    private val calendarInstance = Calendar.getInstance()

    private val journalDetailsViewModel: JournalDetailsViewModel by lazy {
        ViewModelProvider(this).get(JournalDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_input)

        journal = intent.getParcelableExtra(JOURNAL_KEY)
        editJournal = intent.getBooleanExtra(EDIT_KEY, false)

        // Initialize views
        vDiscardButton = findViewById(R.id.discard_button)
        vFinishButton = findViewById(R.id.finish_button)
        vDeleteButton = findViewById(R.id.delete_button)
        vEntryDateButton = findViewById(R.id.date_input)
        vEntryTimeButton = findViewById(R.id.time_input)
        vEntryMoodButton = findViewById(R.id.mood_input)
        vEntryContent = findViewById(R.id.content_input)
        vCameraButton = findViewById(R.id.camera_button)
        vEntryImage = findViewById(R.id.image)

        // Get the photo file of the journal
        journalImage = journalDetailsViewModel.getPhotoFile(journal)

        // Get the URI of the file
        photoUri = FileProvider.getUriForFile(this, "au.edu.swin.sdmd.customprogram.fileprovider", journalImage)

        calendarInstance.timeInMillis = journal.journalDate.time

        // Update all the UI Elements
        updateDateTimeUI()
        updateMoodUI()
        updateJournalImage()
        vEntryContent.setText(journal.journalData)

        if (editJournal) {
            vDeleteButton.visibility = View.VISIBLE
        }


        vCameraButton.apply {

            // Check whether there is any activity for capture image
            val packageManager: PackageManager = this@EntryInputActivity.packageManager
            val captureImage = Intent (MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            // If there is no activity, disable the camera button
            if (resolvedActivity == null)
            {
                isEnabled = false
            }

            setOnClickListener {
                // Provide the file path to the camera app
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivity : List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

                // Grant WRITE permission to the camera app and start the camera activity
                this@EntryInputActivity.grantUriPermission(cameraActivity[0].activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(captureImage, REQUEST_PHOTO)

            }
        }

        // Setup Other Listeners
        vEntryImage.setOnClickListener {
            val i = PhotoDetailsActivity.newIntent(this, journal, true)
            startActivityForResult(i, VIEW_PHOTO)
        }


        vEntryDateButton.setOnClickListener {

            // Pop up a date picker for the user to select date
            DatePickerDialog(this,
                {_, mYear, mMonth, mDay ->
                    calendarInstance.set(mYear, mMonth, mDay)
                    journal.journalDate = calendarInstance.time
                    updateDateTimeUI()
                    },
                calendarInstance.get(Calendar.YEAR),
                calendarInstance.get(Calendar.MONTH),
                calendarInstance.get(Calendar.DAY_OF_MONTH)).show()
        }

        vEntryTimeButton.setOnClickListener {

            // Pop up a time picker for the user to select time
            TimePickerDialog(this,
                {_, hour, minute ->
                    calendarInstance.set(Calendar.HOUR_OF_DAY, hour)
                    calendarInstance.set(Calendar.MINUTE, minute)
                    journal.journalDate = calendarInstance.time
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

            var choice: Int = journal.journalMood

            // Pop up a dialog for the user to select the mood
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.mood))
                .setNeutralButton(resources.getString(R.string.cancel)){_,_ ->}
                .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                    journal.journalMood = choice
                    updateMoodUI()
                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(items, 5 - choice) { _, which ->
                    choice = 5 - which
                }
                .show()
        }

        vFinishButton.setOnClickListener {
            if (vEntryContent.text.isEmpty())
            {
                vEntryContent.error = getString(R.string.empty_input_error)
                vEntryContent.requestFocus()
            }
            else
            {
                journal.journalData = vEntryContent.text.toString()

                if (editJournal) // Check whether user is current in edit journal mode
                {
                    val i = intent.apply {
                        putExtra(JOURNAL_KEY, journal)
                    }

                    journalDetailsViewModel.updateJournal(journal)
                    setResult(Activity.RESULT_OK, i)
                    Toast.makeText(this, getString(R.string.journal_updated), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    journalDetailsViewModel.addJournal(journal)
                    Toast.makeText(this, getString(R.string.journal_added), Toast.LENGTH_SHORT).show()
                }

                finish()
            }

        }

        vDiscardButton.setOnClickListener {
            showDiscardAlert()
        }

        vDeleteButton.setOnClickListener {
            showDeleteAlert()
        }

    }

    /*
        The function will update the UI for the journal mood
     */
    private fun updateMoodUI () {
        when (journal.journalMood)
        {
            1 -> vEntryMoodButton.text = getString(R.string.very_bad_mood_wrap)
            2 -> vEntryMoodButton.text = getString(R.string.bad_mood)
            4 -> vEntryMoodButton.text = getString(R.string.good_mood)
            5 -> vEntryMoodButton.text = getString(R.string.very_good_mood_wrap)
            else -> vEntryMoodButton.text = getString(R.string.neutral_mood)
        }
    }

    /*
        The function will update the UI for the journal date and time
     */
    private fun updateDateTimeUI() {
        formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(journal.journalDate)
        formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(journal.journalDate)
        vEntryDateButton.text = formattedDate
        vEntryTimeButton.text = formattedTime
    }

    /*
        The function will update the UI for the journal image
     */
    private fun updateJournalImage() {
        if (journalImage.exists()) {
            val bitmap = getScaledBitmap(journalImage.path, this)
            vEntryImage.setImageBitmap(bitmap)
        }
        else {
            vEntryImage.setImageDrawable(null)
        }
    }

    /*
        This function will create a discard confirmation dialog and perform action
        according to the user response.
     */
    private fun showDiscardAlert (){

        if (vEntryContent.text.isEmpty() && !editJournal && !journalImage.exists()) // Check whether it is new journal, content is empty and image not exist
        {
            finish()
        }
        else {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.discard_title))
                .setMessage(resources.getString(R.string.discard_supporting_text))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.discard)) { _, _ ->

                    // Delete the image of the journal
                    if (!editJournal && journalImage.exists())
                    {
                        journalImage.delete()
                    }

                    finish()
                }
                .show()
        }

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
                setResult(Activity.RESULT_OK)
                Toast.makeText(this, getString(R.string.journal_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode)
        {
            REQUEST_PHOTO -> {
                this.revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }

        updateJournalImage()

    }

    override fun onBackPressed() {
        showDiscardAlert()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Revoke the URI permission of the camera app
        this.revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    companion object{
        fun newIntent(context: Context?, journal: Journal, editJournal: Boolean): Intent {
            return Intent(context, EntryInputActivity::class.java).apply {
                putExtra(JOURNAL_KEY, journal)
                putExtra(EDIT_KEY, editJournal)
            }
        }
    }
}