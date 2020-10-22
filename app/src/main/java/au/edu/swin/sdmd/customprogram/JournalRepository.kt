package au.edu.swin.sdmd.customprogram

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import au.edu.swin.sdmd.customprogram.database.JournalDatabase
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "journal-database"

class JournalRepository private constructor(context: Context){

    // Create Room Database
    private val database : JournalDatabase = Room.databaseBuilder(
        context.applicationContext,
        JournalDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val journalDao = database.journalDao()
    private val executor = Executors.newSingleThreadExecutor()  // Create a background thread
    private val filesDir = context.applicationContext.filesDir

    /*
        This function retrieves all the journals in the database
        Return: List of Journals
     */
    fun getJournals() : LiveData<List<Journal>> = journalDao.getJournals()

    /*
        This function retrieves the journal with the UUID given
        Parameter:  id - UUID of the journal
        Return: Journal that match the uuid given
     */
    fun getJournal(id: UUID): LiveData<Journal?> = journalDao.getJournal(id)

    /*
        This function retrieves all the journals within the date given
        Parameter:  year - Year of the journal to be retrieve
                    month - Month of the journal to be retrieve
                    day - Day of the journal to be retrieve
        Return: List of journals within the date given
     */
    fun getJournalsByDate(year: Int, month : Int, day : Int) : LiveData<List<Journal>> {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.set(year, month, day, 0, 0,0)
        val start = calendarInstance.timeInMillis
        calendarInstance.set(year, month, day, 23, 59,59)
        val end  = calendarInstance.timeInMillis
        return journalDao.getJournalsByDate(start, end)
    }

    /*
        This function retrieves all the journals within the month given
        Parameter:  year - Year of the journal to be retrieve
                    month - Month of the journal to be retrieve
        Return: List of journals within the month given
     */
    fun getJournalsByMonth(year: Int, month : Int) : LiveData<List<Journal>> {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.set(year, month, 1, 0, 0,0)
        val start = calendarInstance.timeInMillis
        calendarInstance.set(year, month, calendarInstance.getActualMaximum(Calendar.DAY_OF_MONTH) , 23, 59,59)
        val end  = calendarInstance.timeInMillis
        return journalDao.getJournalsByDate(start, end)
    }

    /*
        This function retrieves all the journal that contains the search string
        Parameter:  searchString - query of the search
        Return: List of journals contains the string
     */
    fun getJournalsBySearch(searchString : String) : LiveData<List<Journal>> = journalDao.getJournalsBySearch("%${searchString}%")

    /*
        This function update the details of the journal
        Parameter:  journal - journal to be update
     */
    fun updateJournal (journal: Journal) {
        executor.execute {
            journalDao.updateJournal(journal)
        }
    }

    /*
        This function add a new journal
        Parameter:  journal - journal to be add
     */
    fun addJournal(journal: Journal) {
        executor.execute {
            journalDao.addJournal(journal)
        }
    }

    /*
        This function delete the journal given
        Parameter:  journal - journal to be delete
     */
    fun deleteJournal(journal: Journal) {
        executor.execute {
            journalDao.deleteJournal(journal)
        }
    }

    /*
        This function retrieve the photo file of the journal
        Parameter:   journal - journal required
        Return: Photo File of the journal
     */
    fun getPhotoFile(journal: Journal) : File = File(filesDir, journal.photoFileName)

    companion object {
        private var INSTANCE: JournalRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = JournalRepository(context)
            }
        }

        fun get() : JournalRepository {
            return INSTANCE ?:
            throw IllegalStateException ("JournalRepository must be initialized")
        }
    }
}