package au.edu.swin.sdmd.customprogram

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "journalTable")
data class Journal(@PrimaryKey val id: UUID = UUID.randomUUID(),
                   var journalData : String = "",
                   var journalDate: Date = Calendar.getInstance().time,
                   var journalMood: Int = 3) : Parcelable