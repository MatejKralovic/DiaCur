package me.matejkralovic.diacur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.matejkralovic.diacur.data.entity.Service

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getForVehicle(vehicleId: Long): Flow<List<Service>>

    @Query("SELECT * FROM services ORDER BY date DESC")
    fun getAll(): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getById(id: Long): Service?

    // Total service cost for a vehicle
    @Query("SELECT SUM(cost) FROM services WHERE vehicleId = :vehicleId")
    suspend fun getTotalCost(vehicleId: Long): Double?

    @Insert
    suspend fun insert(service: Service): Long

    @Update
    suspend fun update(service: Service)

    @Delete
    suspend fun delete(service: Service)
}

// Vytvorene pomocou AI
