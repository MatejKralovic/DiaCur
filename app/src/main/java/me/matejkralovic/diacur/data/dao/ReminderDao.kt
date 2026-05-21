package me.matejkralovic.diacur.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.matejkralovic.diacur.data.entity.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE vehicleId = :vehicleId AND completed = 0")
    fun getActiveForVehicle(vehicleId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder?

    @Query("""
        SELECT r.* FROM reminders r
        INNER JOIN vehicles v ON r.vehicleId = v.id
        WHERE r.kmTrigger IS NOT NULL
        AND r.completed = 0
        AND v.odometer >= r.kmTrigger
    """)
    suspend fun getTriggeredByOdometer(): List<Reminder>

    @Query("""
        SELECT * FROM reminders 
        WHERE dateTrigger IS NOT NULL 
        AND completed = 0 
        AND dateTrigger <= :nowMillis
    """)
    suspend fun getTriggeredByDate(nowMillis: Long): List<Reminder>

    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("UPDATE reminders SET completed = 1 WHERE id = :id")
    suspend fun markAsCompleted(id: Long)
}

// Vytvorene pomocou AI
