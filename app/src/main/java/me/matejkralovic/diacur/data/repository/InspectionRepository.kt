package me.matejkralovic.diacur.data.repository

import me.matejkralovic.diacur.data.dao.InspectionDao
import me.matejkralovic.diacur.data.entity.Inspection
import kotlinx.coroutines.flow.Flow

class InspectionRepository(private val dao: InspectionDao) {

    fun getForVehicle(vehicleId: Long): Flow<List<Inspection>> = dao.getForVehicle(vehicleId)

    suspend fun getById(id: Long): Inspection? = dao.getById(id)

    suspend fun getDueForNotification(nowMillis: Long): List<Inspection> =
        dao.getDueForNotification(nowMillis)

    suspend fun insert(inspection: Inspection): Long = dao.insert(inspection)

    suspend fun update(inspection: Inspection) = dao.update(inspection)

    suspend fun delete(inspection: Inspection) = dao.delete(inspection)

    suspend fun getTotalCost(vehicleId: Long): Double = dao.getTotalCost(vehicleId) ?: 0.0
}

// Vytvorene pomocou AI
