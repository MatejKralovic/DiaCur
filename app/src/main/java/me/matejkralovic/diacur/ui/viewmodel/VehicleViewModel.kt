package me.matejkralovic.diacur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.data.entity.Vehicle
import me.matejkralovic.diacur.data.entity.VehicleType
import me.matejkralovic.diacur.data.repository.FuelingRepository
import me.matejkralovic.diacur.data.repository.ServiceRepository
import me.matejkralovic.diacur.data.repository.VehicleRepository

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val fuelingRepository: FuelingRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = vehicleRepository.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addVehicle(
        name: String,
        brand: String,
        model: String,
        odometer: Int,
        type: VehicleType
    ) {
        viewModelScope.launch {
            vehicleRepository.insert(
                Vehicle(
                    name = name,
                    brand = brand,
                    model = model,
                    odometer = odometer,
                    type = type
                )
            )
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch { vehicleRepository.update(vehicle) }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch { vehicleRepository.delete(vehicle) }
    }

    suspend fun getTotalCost(vehicleId: Long): Double {
        val fuelingCost = fuelingRepository.getTotalCost(vehicleId)
        val serviceCost = serviceRepository.getTotalCost(vehicleId)
        return fuelingCost + serviceCost
    }

    companion object {
        fun factory(
            vehicleRepository: VehicleRepository,
            fuelingRepository: FuelingRepository,
            serviceRepository: ServiceRepository
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                VehicleViewModel(vehicleRepository, fuelingRepository, serviceRepository) as T
        }
    }
}

// Vytvorene pomocou AI

