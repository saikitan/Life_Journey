package au.edu.swin.sdmd.customprogram.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import au.edu.swin.sdmd.customprogram.Journal

@Database (entities = [Journal::class], version = 1)
@TypeConverters (JournalTypeConverters::class)
abstract class JournalDatabase : RoomDatabase() {

    abstract fun journalDao() : JournalDao
}