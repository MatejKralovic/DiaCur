package me.matejkralovic.diacur.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.matejkralovic.diacur.R

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val model: String,
    val odometer: Int,          // km
    val type: VehicleType
)

enum class VehicleType(val labelRes: Int) {
    CAR        (R.string.vehicle_type_car),
    MOTORCYCLE (R.string.vehicle_type_motorcycle),
    VAN        (R.string.vehicle_type_van),
    OTHER      (R.string.vehicle_type_other)
}

// Vytvorene pomocou AI
// uprava enumu, aby obsahoval resource
