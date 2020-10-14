package au.edu.swin.sdmd.customprogram

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class JournalListViewModel : ViewModel() {

    private val journalRepository = JournalRepository.get()
    lateinit var journalListLiveData : LiveData<List<Journal>>

    fun getAllJournals() : LiveData<List<Journal>> {
        journalListLiveData = journalRepository.getJournals()
        return journalListLiveData
    }

    fun getAllJournalsByDate(year : Int, month : Int, day : Int) : LiveData<List<Journal>> {
        journalListLiveData = journalRepository.getJournalsByDate(year, month, day)
        return journalListLiveData
    }

    fun getAllJournalsByMonth(year : Int, month : Int) : LiveData<List<Journal>> {
        journalListLiveData = journalRepository.getJournalsByMonth(year, month)
        return journalListLiveData
    }
}