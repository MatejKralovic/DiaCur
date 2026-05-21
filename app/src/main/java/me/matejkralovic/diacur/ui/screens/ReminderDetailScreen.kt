package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Reminder
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    navController: NavController,
    reminderId: Long,
    viewModel: ReminderViewModel
) {
    var reminder by remember { mutableStateOf<Reminder?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reminderId) {
        reminder = viewModel.getById(reminderId)
        loaded = true
    }

    if (loaded && reminder == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    reminder?.let { rem ->
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.deleteReminder(rem)
                    navController.popBackStack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.reminder_detail_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { editMode = !editMode }) {
                            Icon(
                                if (editMode) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (editMode) {
                ReminderEditForm(
                    reminder = rem,
                    modifier = Modifier.padding(innerPadding),
                    onSave = { updated ->
                        viewModel.updateReminder(updated)
                        editMode = false
                    },
                    onCancel = { editMode = false }
                )
            } else {
                ReminderInfo(
                    reminder = rem,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ── Reminder info (read-only) ─────────────────────────────────
@Composable
private fun ReminderInfo(
    reminder: Reminder,
    modifier: Modifier = Modifier
) {
    val tasks = remember(reminder.serviceTasks) { ServiceTask.fromBitmask(reminder.serviceTasks) }
    val dateFormatted = remember(reminder.dateTrigger) {
        reminder.dateTrigger?.let {
            SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(it))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReminderInfoRow(
            label = stringResource(R.string.reminder_description),
            value = reminder.description
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        if (reminder.kmTrigger != null) {
            ReminderInfoRow(
                label = stringResource(R.string.reminder_km_trigger),
                value = "${reminder.kmTrigger} km"
            )
        }

        if (dateFormatted != null) {
            ReminderInfoRow(
                label = stringResource(R.string.reminder_date_trigger),
                value = dateFormatted
            )
        }

        if (tasks.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = stringResource(R.string.service_tasks_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
            tasks.forEach { task ->
                Text(
                    text = "• ${stringResource(task.labelRes)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ReminderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Edit form ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderEditForm(
    reminder: Reminder,
    modifier: Modifier = Modifier,
    onSave: (Reminder) -> Unit,
    onCancel: () -> Unit
) {
    var description by remember { mutableStateOf(reminder.description) }
    var selectedTasks by remember { mutableStateOf(ServiceTask.fromBitmask(reminder.serviceTasks)) }
    var showTaskPicker by remember { mutableStateOf(false) }

    var kmTriggerEnabled by remember { mutableStateOf(reminder.kmTrigger != null) }
    var kmTrigger by remember { mutableStateOf(reminder.kmTrigger?.toString() ?: "") }

    var dateTriggerEnabled by remember { mutableStateOf(reminder.dateTrigger != null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = reminder.dateTrigger ?: System.currentTimeMillis()
    )

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
            onConfirm = { tasks -> selectedTasks = tasks; showTaskPicker = false },
            onDismiss = { showTaskPicker = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

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
            Text(stringResource(R.string.reminder_km_trigger), style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = kmTriggerEnabled,
                onCheckedChange = { kmTriggerEnabled = it; triggerError = false; if (!it) kmTrigger = "" }
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
            Text(stringResource(R.string.reminder_date_trigger), style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = dateTriggerEnabled,
                onCheckedChange = { dateTriggerEnabled = it; triggerError = false }
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
                        Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.pick_date))
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

        OutlinedButton(
            onClick = { showTaskPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.pick_tasks))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    descriptionError = description.isBlank()
                    triggerError = !kmTriggerEnabled && !dateTriggerEnabled
                    kmTriggerError = kmTriggerEnabled && kmTrigger.isBlank()

                    if (!descriptionError && !triggerError && !kmTriggerError) {
                        onSave(
                            reminder.copy(
                                description = description.trim(),
                                serviceTasks = ServiceTask.toBitmask(selectedTasks),
                                kmTrigger = if (kmTriggerEnabled) kmTrigger.toInt() else null,
                                dateTrigger = if (dateTriggerEnabled)
                                    datePickerState.selectedDateMillis
                                else null
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
// Vytvorene pomocou AI
