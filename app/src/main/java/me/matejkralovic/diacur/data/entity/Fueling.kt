package me.matejkralovic.diacur.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuelings",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class Fueling(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val volume: Double,             // litres
    val pricePerLitre: Double,      // EUR/l
    val date: Long,                 // epoch millis
    val odometer: Int,              // km at time of fueling
    val note: String = "",
    val latitude: Double? = null,   // GPS – null if not recorded
    val longitude: Double? = null
)

// Vytvorene pomocou AI
