package me.matejkralovic.diacur.data.repository

import me.matejkralovic.diacur.data.dao.VehicleDao
import me.matejkralovic.diacur.data.entity.Vehicle
import kotlinx.coroutines.flow.Flow

class VehicleRepository(private val dao: VehicleDao) {

    val all: Flow<List<Vehicle>> = dao.getAll()

    suspend fun getById(id: Long): Vehicle? = dao.getById(id)

    suspend fun insert(vehicle: Vehicle): Long = dao.insert(vehicle)

    suspend fun update(vehicle: Vehicle) = dao.update(vehicle)

    suspend fun delete(vehicle: Vehicle) = dao.delete(vehicle)

    suspend fun updateOdometer(id: Long, km: Int) = dao.updateOdometer(id, km)
}

// Vytvorene pomocou AI
