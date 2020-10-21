package au.edu.swin.sdmd.customprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class JournalDetailsViewModel : ViewModel() {

    private val journalRepository = JournalRepository.get()

    fun loadJournal(journalId : UUID) = journalRepository.getJournal(journalId)

    fun getPhotoFile(journal: Journal) : File {
        return journalRepository.getPhotoFile(journal)
    }
}