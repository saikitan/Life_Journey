package au.edu.swin.sdmd.customprogram

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Journal(@PrimaryKey val id: UUID = UUID.randomUUID(),
                   var data : String = " ",
                   var date: Date,
                   val mood: Int)