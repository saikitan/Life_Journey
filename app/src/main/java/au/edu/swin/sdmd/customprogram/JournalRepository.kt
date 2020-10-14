package au.edu.swin.sdmd.customprogram

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import au.edu.swin.sdmd.customprogram.database.JournalDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "journal-database"

class JournalRepository private constructor(context: Context){

    // Room.databaseBuilder() creates a concrete implementation of abstract JournalDatabase
    private val database : JournalDatabase = Room.databaseBuilder(
        context.applicationContext,
        JournalDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val journalDao = database.journalDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getJournals() : LiveData<List<Journal>> = journalDao.getJournals()

    fun getJournal(id: UUID): LiveData<Journal?> = journalDao.getJournal(id)

    fun getJournalsByDate(year: Int, month : Int, day : Int) : LiveData<List<Journal>> {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.set(year, month, day, 0, 0,0)
        val start = calendarInstance.timeInMillis
        calendarInstance.set(year, month, day, 23, 59,59)
        val end  = calendarInstance.timeInMillis
        return journalDao.getJournalsByDate(start, end)
    }

    fun updateJournal (journal: Journal) {
        executor.execute {
            journalDao.updateJournal(journal)
        }
    }

    fun addJournal(journal: Journal) {
        executor.execute {
            journalDao.addJournal(journal)
        }
    }

    fun deleteJournal(journal: Journal) {
        executor.execute {
            journalDao.deleteJournal(journal)
        }
    }

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