package me.matejkralovic.diacur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.matejkralovic.diacur.data.entity.Inspection

@Dao
interface InspectionDao {

    @Query("SELECT * FROM inspections WHERE vehicleId = :vehicleId ORDER BY expiryDate DESC")
    fun getForVehicle(vehicleId: Long): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getById(id: Long): Inspection?

    @Query("""
        SELECT * FROM inspections 
        WHERE notifyBeforeExpiry = 1 
        AND notificationDate IS NOT NULL 
        AND notificationDate <= :nowMillis 
        AND expiryDate >= :nowMillis
    """)
    suspend fun getDueForNotification(nowMillis: Long): List<Inspection>

    @Insert
    suspend fun insert(inspection: Inspection): Long

    @Update
    suspend fun update(inspection: Inspection)

    @Delete
    suspend fun delete(inspection: Inspection)
}

// Vytvorene pomocou AI
