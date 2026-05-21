package me.matejkralovic.diacur.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val description: String,
    val serviceTasks: Int = 0,      // bitmask of ServiceTask bits, 0 = no specific tasks
    val kmTrigger: Int? = null,     // null = do not trigger by odometer
    val dateTrigger: Long? = null,  // null = do not trigger by date
    val completed: Boolean = false
)
// Vytvorene pomocou AI
