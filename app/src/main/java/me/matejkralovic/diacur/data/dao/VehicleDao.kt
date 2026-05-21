package me.matejkralovic.diacur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.matejkralovic.diacur.data.entity.Vehicle

@Dao
interface VehicleDao {

    @Query("SELECT * FROM vehicles ORDER BY name ASC")
    fun getAll(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getById(id: Long): Vehicle?

    @Insert
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Query("UPDATE vehicles SET odometer = :km WHERE id = :id")
    suspend fun updateOdometer(id: Long, km: Int)
}

// Vytvorene pomocou AI
