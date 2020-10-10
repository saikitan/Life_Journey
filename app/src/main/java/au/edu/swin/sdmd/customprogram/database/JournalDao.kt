package au.edu.swin.sdmd.customprogram.database

import androidx.room.Dao
import androidx.room.Query
import au.edu.swin.sdmd.customprogram.Journal

@Dao
interface JournalDao {

    @Query("SELECT * From journal")
    fun getJournals() : List <Journal>
}