package au.edu.swin.sdmd.customprogram

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "journal_table")
data class Journal(@PrimaryKey val id: UUID = UUID.randomUUID(),
                   var journalData : String = " ",
                   var journalDate: Date = Calendar.getInstance().time,
                   var journalMood: Int = 3)