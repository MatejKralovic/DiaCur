package me.matejkralovic.diacur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.data.entity.Fueling
import me.matejkralovic.diacur.data.repository.FuelingRepository
import me.matejkralovic.diacur.data.repository.VehicleRepository

class FuelingViewModel(
    private val fuelingRepository: FuelingRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    fun getFuelingsForVehicle(vehicleId: Long): StateFlow<List<Fueling>> =
        fuelingRepository.getForVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addFueling(
        vehicleId: Long,
        volume: Double,
        pricePerLitre: Double,
        odometer: Int,
        date: Long,
        note: String,
        latitude: Double?,
        longitude: Double?
    ): AddFuelingResult {
        val last = fuelingRepository.getLast(vehicleId)
        if (last != null && odometer < last.odometer) {
            return AddFuelingResult.InvalidOdometer(last.odometer)
        }

        fuelingRepository.insert(
            Fueling(
                vehicleId = vehicleId,
                volume = volume,
                pricePerLitre = pricePerLitre,
                odometer = odometer,
                date = date,
                note = note,
                latitude = latitude,
                longitude = longitude
            )
        )
        vehicleRepository.updateOdometer(vehicleId, odometer)
        return AddFuelingResult.Success
    }

    fun updateFueling(fueling: Fueling) {
        viewModelScope.launch { fuelingRepository.update(fueling) }
    }

    fun deleteFueling(fueling: Fueling) {
        viewModelScope.launch { fuelingRepository.delete(fueling) }
    }

    suspend fun getById(id: Long): Fueling? = fuelingRepository.getById(id)

    suspend fun getAvgConsumption(vehicleId: Long): Double? {
        val fuelings = fuelingRepository.getForVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .value
            .sortedBy { it.date }

        if (fuelings.size < 2) return null
        val firstKm = fuelings.first().odometer
        val lastKm = fuelings.last().odometer
        if (lastKm == firstKm) return null
        val totalVolume = fuelings.drop(1).sumOf { it.volume }
        return (totalVolume / (lastKm - firstKm)) * 100
    }

    companion object {
        fun factory(
            fuelingRepository: FuelingRepository,
            vehicleRepository: VehicleRepository
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FuelingViewModel(fuelingRepository, vehicleRepository) as T
        }
    }
}

sealed class AddFuelingResult {
    data object Success : AddFuelingResult()
    data class InvalidOdometer(val lastOdometer: Int) : AddFuelingResult()
}

// Vytvorene pomocou AI

