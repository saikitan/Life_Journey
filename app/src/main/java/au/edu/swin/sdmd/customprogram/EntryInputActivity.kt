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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
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
    private lateinit var vEntryContentInput : EditText
    private lateinit var vCameraButton: ImageButton
    private lateinit var vJournalImage: ImageView
    private lateinit var journalEntry : Journal
    private lateinit var formattedDate : String
    private lateinit var formattedTime: String
    private lateinit var journalImage : File
    private lateinit var photoUri : Uri
    private var editJournal = false
    private val calendarInstance = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_input)

        journalEntry = intent.getParcelableExtra(JOURNAL_KEY)
        editJournal = intent.getBooleanExtra(EDIT_KEY, false)

        vDiscardButton = findViewById(R.id.discard_button)
        vFinishButton = findViewById(R.id.finish_button)
        vDeleteButton = findViewById(R.id.delete_button)
        vEntryDateButton = findViewById(R.id.date_input)
        vEntryTimeButton = findViewById(R.id.time_input)
        vEntryMoodButton = findViewById(R.id.mood_input)
        vEntryContentInput = findViewById(R.id.content_input)
        vCameraButton = findViewById(R.id.camera_button)
        vJournalImage = findViewById(R.id.image)

        journalImage = JournalRepository.get().getPhotoFile(journalEntry)
        photoUri = FileProvider.getUriForFile(this, "au.edu.swin.sdmd.customprogram.fileprovider", journalImage)

        calendarInstance.timeInMillis = journalEntry.journalDate.time

        updateDateTimeUI()
        updateMoodUI()
        updateJournalImage()

        vEntryContentInput.setText(journalEntry.journalData)

        if (editJournal) {
            vDeleteButton.visibility = View.VISIBLE
        }

        vCameraButton.apply {
            val packageManager: PackageManager = this@EntryInputActivity.packageManager

            val captureImage = Intent (MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null)
            {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivity : List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

                for (activity in cameraActivity) {
                    this@EntryInputActivity.grantUriPermission(activity.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    startActivityForResult(captureImage, REQUEST_PHOTO)
                }
            }
        }

        vJournalImage.setOnClickListener {
            val i = PhotoDetailsActivity.newIntent(this, journalEntry, true)
            startActivityForResult(i, VIEW_PHOTO)
        }


        vEntryDateButton.setOnClickListener {
            DatePickerDialog(this,
                {_, mYear, mMonth, mDay ->
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
                {_, hour, minute ->
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
            showMoodSelectionDialog()
        }

        vFinishButton.setOnClickListener {
            if (vEntryContentInput.text.isEmpty())
            {
                vEntryContentInput.error = getString(R.string.empty_input_error)
                vEntryContentInput.requestFocus()
            }
            else
            {
                journalEntry.journalData = vEntryContentInput.text.toString()

                if (editJournal)
                {
                    val i = intent.apply {
                        putExtra(JOURNAL_KEY, journalEntry)
                    }

                    JournalRepository.get().updateJournal(journalEntry)
                    setResult(Activity.RESULT_OK, i)
                }
                else
                {
                    JournalRepository.get().addJournal(journalEntry)
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

    private fun updateJournalImage() {
        if (journalImage.exists()) {
            val bitmap = getScaledBitmap(journalImage.path, this)
            vJournalImage.setImageBitmap(bitmap)
        }
        else {
            vJournalImage.setImageDrawable(null)
        }
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

    private fun showMoodSelectionDialog() {
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

    private fun showDiscardAlert (){
        if (vEntryContentInput.text.isEmpty() && !editJournal && !journalImage.exists())
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

                    if (!editJournal && journalImage.exists())
                    {
                        journalImage.delete()
                    }

                    finish()
                }
                .show()
        }

    }

    private fun showDeleteAlert (){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.delete_title))
            .setMessage(resources.getString(R.string.delete_supporting_text))
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                JournalRepository.get().deleteJournal(journalEntry)
                journalImage.delete()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .show()

    }

    override fun onBackPressed() {
        showDiscardAlert()
    }

    override fun onDestroy() {
        super.onDestroy()
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