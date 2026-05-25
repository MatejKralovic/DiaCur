package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.viewmodel.ReminderViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderAddScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: ReminderViewModel
) {
    var description by rememberSaveable { mutableStateOf("") }
    var selectedTasks by rememberSaveable { mutableStateOf(emptySet<ServiceTask>()) }
    var showTaskPicker by remember { mutableStateOf(false) }

    // Km trigger
    var kmTriggerEnabled by rememberSaveable { mutableStateOf(false) }
    var kmTrigger by rememberSaveable { mutableStateOf("") }

    // Date trigger
    var dateTriggerEnabled by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Errors
    var descriptionError by remember { mutableStateOf(false) }
    var triggerError by remember { mutableStateOf(false) }
    var kmTriggerError by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTaskPicker) {
        ServiceTaskPickerDialog(
            selectedTasks = selectedTasks,
            onConfirm = { tasks ->
                selectedTasks = tasks
                showTaskPicker = false
            },
            onDismiss = { showTaskPicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_reminder)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; descriptionError = false },
                label = { Text(stringResource(R.string.reminder_description)) },
                isError = descriptionError,
                supportingText = if (descriptionError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            // Km trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.reminder_km_trigger),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = kmTriggerEnabled,
                    onCheckedChange = {
                        kmTriggerEnabled = it
                        triggerError = false
                        if (!it) kmTrigger = ""
                    }
                )
            }

            if (kmTriggerEnabled) {
                OutlinedTextField(
                    value = kmTrigger,
                    onValueChange = { kmTrigger = it.filter { c -> c.isDigit() }; kmTriggerError = false },
                    label = { Text(stringResource(R.string.vehicle_odometer)) },
                    isError = kmTriggerError,
                    supportingText = if (kmTriggerError) {
                        { Text(stringResource(R.string.field_required)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("km") }
                )
            }

            HorizontalDivider()

            // Date trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.reminder_date_trigger),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = dateTriggerEnabled,
                    onCheckedChange = {
                        dateTriggerEnabled = it
                        triggerError = false
                    }
                )
            }

            if (dateTriggerEnabled) {
                OutlinedTextField(
                    value = datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86400000)
                        "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.date)) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.pick_date)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (triggerError) {
                Text(
                    text = stringResource(R.string.reminder_trigger_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider()

            // Selected tasks summary
            if (selectedTasks.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        selectedTasks.forEach { task ->
                            Text(
                                text = "• ${stringResource(task.labelRes)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Pick tasks button (optional)
            OutlinedButton(
                onClick = { showTaskPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_tasks))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    descriptionError = description.isBlank()
                    triggerError = !kmTriggerEnabled && !dateTriggerEnabled
                    kmTriggerError = kmTriggerEnabled && kmTrigger.isBlank()

                    if (!descriptionError && !triggerError && !kmTriggerError) {
                        viewModel.addReminder(
                            vehicleId = vehicleId,
                            description = description.trim(),
                            serviceTasks = selectedTasks,
                            kmTrigger = if (kmTriggerEnabled) kmTrigger.toInt() else null,
                            dateTrigger = if (dateTriggerEnabled)
                                datePickerState.selectedDateMillis
                            else null
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(stringResource(R.string.add))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
// Vytvorene pomocou AI
