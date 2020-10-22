package au.edu.swin.sdmd.customprogram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class PhotoDetailsActivity : AppCompatActivity() {

    private lateinit var vJournalImage : ImageView
    private lateinit var vBackButton: ImageButton
    private lateinit var vDeleteButton: ImageButton
    private lateinit var journalImage : File
    private lateinit var journalEntry : Journal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        // Initialize views
        vJournalImage = findViewById(R.id.image)
        vBackButton = findViewById(R.id.back_button)
        vDeleteButton = findViewById(R.id.delete_button)

        journalEntry = intent.getParcelableExtra(JOURNAL_KEY)
        journalImage = JournalRepository.get().getPhotoFile(journalEntry)

        if (intent.getBooleanExtra(EDIT_KEY, false))
        {
            vDeleteButton.visibility = View.VISIBLE
        }

        // Display the image
        if (journalImage.exists()) {
            val bitmap = getScaledBitmap(journalImage.path, this)
            vJournalImage.setImageBitmap(bitmap)
        }
        else {
            vJournalImage.setImageDrawable(null)
        }

        // Setup the listeners
        vDeleteButton.setOnClickListener {
            showDeleteAlert()
        }

        vBackButton.setOnClickListener {
            finish()
        }
    }

    /*
        This function will create a delete confirmation box and perform
        action according to the user response
     */
    private fun showDeleteAlert (){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.delete_title))
            .setMessage(resources.getString(R.string.delete_image_supporting_text))
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                journalImage.delete()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .show()

    }

    companion object{
        fun newIntent(context: Context?, journal: Journal, editJournal: Boolean): Intent {
            return Intent(context, PhotoDetailsActivity::class.java).apply {
                putExtra(JOURNAL_KEY, journal)
                putExtra(EDIT_KEY, editJournal)
            }
        }
    }
}