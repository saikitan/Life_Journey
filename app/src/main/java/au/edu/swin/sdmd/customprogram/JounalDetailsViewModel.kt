package au.edu.swin.sdmd.customprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.*

class JournalDetailsViewModel : ViewModel() {

    private val journalRepository = JournalRepository.get()

    fun addJournal(journal: Journal) = journalRepository.addJournal(journal)

    fun updateJournal(journal: Journal) = journalRepository.updateJournal(journal)

    fun deleteJournal(journal: Journal) = journalRepository.deleteJournal(journal)

    fun getJournal(journalId: UUID): LiveData<Journal?> = journalRepository.getJournal(journalId)

    fun getPhotoFile(journal: Journal) = journalRepository.getPhotoFile(journal)
}