package au.edu.swin.sdmd.customprogram.database

import androidx.lifecycle.LiveData
import androidx.room.*
import au.edu.swin.sdmd.customprogram.Journal
import java.util.*

@Dao
interface JournalDao {

    @Query("SELECT * From journalTable ORDER BY journalDate DESC")
    fun getJournals() : LiveData<List<Journal>>

    @Query("SELECT * From journalTable WHERE journalDate BETWEEN (:start) AND (:end)  ORDER BY journalDate DESC")
    fun getJournalsByDate(start : Long, end: Long) : LiveData<List<Journal>>

    @Query("SELECT * FROM journalTable WHERE id=(:id)")
    fun getJournal(id: UUID): LiveData<Journal?>

    @Insert
    fun addJournal(journal: Journal)

    @Update
    fun updateJournal(journal: Journal)

    @Delete
    fun deleteJournal(journal: Journal)


}