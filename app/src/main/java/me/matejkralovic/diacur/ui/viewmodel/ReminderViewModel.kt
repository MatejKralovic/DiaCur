package me.matejkralovic.diacur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.data.entity.Reminder
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.data.repository.ReminderRepository

class ReminderViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    fun getRemindersForVehicle(vehicleId: Long): StateFlow<List<Reminder>> =
        reminderRepository.getActiveForVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(
        vehicleId: Long,
        description: String,
        serviceTasks: Set<ServiceTask>,
        kmTrigger: Int?,
        dateTrigger: Long?
    ) {
        viewModelScope.launch {
            reminderRepository.insert(
                Reminder(
                    vehicleId = vehicleId,
                    description = description,
                    serviceTasks = ServiceTask.toBitmask(serviceTasks),
                    kmTrigger = kmTrigger,
                    dateTrigger = dateTrigger
                )
            )
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch { reminderRepository.update(reminder) }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch { reminderRepository.delete(reminder) }
    }

    fun markAsCompleted(id: Long) {
        viewModelScope.launch { reminderRepository.markAsCompleted(id) }
    }

    companion object {
        fun factory(reminderRepository: ReminderRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ReminderViewModel(reminderRepository) as T
            }
    }
}

// Vytvorene pomocou AI

