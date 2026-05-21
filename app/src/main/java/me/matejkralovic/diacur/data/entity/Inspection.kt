package me.matejkralovic.diacur.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import me.matejkralovic.diacur.R

@Entity(
    tableName = "inspections",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class Inspection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val type: InspectionType,
    val cost: Double,
    val startDate: Long,
    val expiryDate: Long,
    val notifyBeforeExpiry: Boolean = true,
    val notificationDate: Long? = null
)

enum class InspectionType(val labelRes: Int) {
    STK (R.string.inspection_type_stk),
    EK  (R.string.inspection_type_ek)
}

// Vytvorene pomocou AI
// Manualna uprava enumu - pridanie resource