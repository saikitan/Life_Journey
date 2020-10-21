package au.edu.swin.sdmd.customprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.io.File

class JournalListViewModel : ViewModel() {

    private val journalRepository = JournalRepository.get()

    fun getAllJournals() : LiveData<List<Journal>> = journalRepository.getJournals()

    fun getAllJournalsByDate(year : Int, month : Int, day : Int) : LiveData<List<Journal>> = journalRepository.getJournalsByDate(year, month, day)

    fun getAllJournalsByMonth(year : Int, month : Int) : LiveData<List<Journal>> = journalRepository.getJournalsByMonth(year, month)

    fun getAllJournalBySearch(searchString : String) : LiveData<List<Journal>> = journalRepository.getJournalsBySearch(searchString)

}