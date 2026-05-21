package me.matejkralovic.diacur.data.repository

import me.matejkralovic.diacur.data.dao.FuelingDao
import me.matejkralovic.diacur.data.entity.Fueling
import kotlinx.coroutines.flow.Flow

class FuelingRepository(private val dao: FuelingDao) {

    fun getForVehicle(vehicleId: Long): Flow<List<Fueling>> = dao.getForVehicle(vehicleId)

    fun getAll(): Flow<List<Fueling>> = dao.getAll()

    suspend fun getById(id: Long): Fueling? = dao.getById(id)

    suspend fun getLast(vehicleId: Long): Fueling? = dao.getLastForVehicle(vehicleId)

    suspend fun getTotalCost(vehicleId: Long): Double = dao.getTotalCost(vehicleId) ?: 0.0

    suspend fun insert(fueling: Fueling): Long = dao.insert(fueling)

    suspend fun update(fueling: Fueling) = dao.update(fueling)

    suspend fun delete(fueling: Fueling) = dao.delete(fueling)
}

// Vytvorene pomocou AI
