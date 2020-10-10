package au.edu.swin.sdmd.customprogram

import android.content.Context
import androidx.room.Room
import au.edu.swin.sdmd.customprogram.database.JournalDatabase
import java.lang.IllegalStateException

private const val DATABASE_NAME = "journal-database"

class JournalRepository private constructor(context: Context){

    // Room.databaseBuilder() creates a concrete implementation of abstract JournalDatabase
    private val database : JournalDatabase = Room.databaseBuilder(
        context.applicationContext,
        JournalDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val journalDao = database.journalDao()

    fun getJournals() : List<Journal> = journalDao.getJournals()

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