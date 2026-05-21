package me.matejkralovic.diacur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.matejkralovic.diacur.data.entity.Fueling

@Dao
interface FuelingDao {

    @Query("SELECT * FROM fuelings WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getForVehicle(vehicleId: Long): Flow<List<Fueling>>

    @Query("SELECT * FROM fuelings ORDER BY date DESC")
    fun getAll(): Flow<List<Fueling>>

    @Query("SELECT * FROM fuelings WHERE id = :id")
    suspend fun getById(id: Long): Fueling?

    // Used to validate odometer when adding a new fueling
    @Query("SELECT * FROM fuelings WHERE vehicleId = :vehicleId ORDER BY date DESC LIMIT 1")
    suspend fun getLastForVehicle(vehicleId: Long): Fueling?

    // Total fuel cost for a vehicle
    @Query("SELECT SUM(volume * pricePerLitre) FROM fuelings WHERE vehicleId = :vehicleId")
    suspend fun getTotalCost(vehicleId: Long): Double?

    @Insert
    suspend fun insert(fueling: Fueling): Long

    @Update
    suspend fun update(fueling: Fueling)

    @Delete
    suspend fun delete(fueling: Fueling)
}

// Vytvorene pomocou AI
