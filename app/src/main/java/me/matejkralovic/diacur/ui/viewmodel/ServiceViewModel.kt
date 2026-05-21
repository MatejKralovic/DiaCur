package me.matejkralovic.diacur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.data.entity.Service
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.data.repository.ServiceRepository

class ServiceViewModel(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    fun getServicesForVehicle(vehicleId: Long): StateFlow<List<Service>> =
        serviceRepository.getForVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addService(
        vehicleId: Long,
        cost: Double,
        date: Long,
        tasks: Set<ServiceTask>,
        note: String
    ) {
        viewModelScope.launch {
            serviceRepository.insert(
                Service(
                    vehicleId = vehicleId,
                    cost = cost,
                    date = date,
                    tasks = ServiceTask.toBitmask(tasks),
                    note = note
                )
            )
        }
    }

    fun updateService(service: Service) {
        viewModelScope.launch { serviceRepository.update(service) }
    }

    fun deleteService(service: Service) {
        viewModelScope.launch { serviceRepository.delete(service) }
    }

    suspend fun getById(id: Long): Service? = serviceRepository.getById(id)

    companion object {
        fun factory(serviceRepository: ServiceRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ServiceViewModel(serviceRepository) as T
            }
    }
}

// Vytvorene pomocou AI
