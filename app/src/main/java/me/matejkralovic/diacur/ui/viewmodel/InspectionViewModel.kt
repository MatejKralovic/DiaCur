package me.matejkralovic.diacur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.data.entity.Inspection
import me.matejkralovic.diacur.data.entity.InspectionType
import me.matejkralovic.diacur.data.repository.InspectionRepository

class InspectionViewModel(
    private val inspectionRepository: InspectionRepository
) : ViewModel() {

    fun getInspectionsForVehicle(vehicleId: Long): StateFlow<List<Inspection>> =
        inspectionRepository.getForVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addInspection(
        vehicleId: Long,
        type: InspectionType,
        cost: Double,
        startDate: Long,
        expiryDate: Long,
        notifyBeforeExpiry: Boolean,
        notificationDate: Long?
    ) {
        viewModelScope.launch {
            inspectionRepository.insert(
                Inspection(
                    vehicleId = vehicleId,
                    type = type,
                    cost = cost,
                    startDate = startDate,
                    expiryDate = expiryDate,
                    notifyBeforeExpiry = notifyBeforeExpiry,
                    notificationDate = notificationDate
                )
            )
        }
    }

    fun updateInspection(inspection: Inspection) {
        viewModelScope.launch { inspectionRepository.update(inspection) }
    }

    fun deleteInspection(inspection: Inspection) {
        viewModelScope.launch { inspectionRepository.delete(inspection) }
    }

    companion object {
        fun factory(inspectionRepository: InspectionRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    InspectionViewModel(inspectionRepository) as T
            }
    }
}

// Vytvorene pomocou AI
