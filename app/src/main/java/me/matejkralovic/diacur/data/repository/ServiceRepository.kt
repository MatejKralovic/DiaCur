package me.matejkralovic.diacur.data.repository

import me.matejkralovic.diacur.data.dao.ServiceDao
import me.matejkralovic.diacur.data.entity.Service
import kotlinx.coroutines.flow.Flow

class ServiceRepository(private val dao: ServiceDao) {

    fun getForVehicle(vehicleId: Long): Flow<List<Service>> = dao.getForVehicle(vehicleId)

    fun getAll(): Flow<List<Service>> = dao.getAll()

    suspend fun getById(id: Long): Service? = dao.getById(id)

    suspend fun getTotalCost(vehicleId: Long): Double = dao.getTotalCost(vehicleId) ?: 0.0

    suspend fun insert(service: Service): Long = dao.insert(service)

    suspend fun update(service: Service) = dao.update(service)

    suspend fun delete(service: Service) = dao.delete(service)
}

// Vytvorene pomocou AI
