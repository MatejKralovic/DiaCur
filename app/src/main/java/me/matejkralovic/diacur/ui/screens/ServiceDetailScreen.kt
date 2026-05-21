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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Service
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.viewmodel.ServiceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    navController: NavController,
    serviceId: Long,
    viewModel: ServiceViewModel
) {
    var service by remember { mutableStateOf<Service?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(serviceId) {
        service = viewModel.getById(serviceId)
        loaded = true
    }

    if (loaded && service == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    service?.let { s ->
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.deleteService(s)
                    navController.popBackStack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.service_detail_title)) },
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
                ServiceEditForm(
                    service = s,
                    modifier = Modifier.padding(innerPadding),
                    onSave = { updated ->
                        viewModel.updateService(updated)
                        editMode = false
                    },
                    onCancel = { editMode = false }
                )
            } else {
                ServiceInfo(
                    service = s,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ── Service info (read-only) ──────────────────────────────────
@Composable
private fun ServiceInfo(
    service: Service,
    modifier: Modifier = Modifier
) {
    val dateFormatted = remember(service.date) {
        SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(service.date))
    }
    val tasks = remember(service.tasks) { ServiceTask.fromBitmask(service.tasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ServiceInfoRow(
            label = stringResource(R.string.date),
            value = dateFormatted
        )
        ServiceInfoRow(
            label = stringResource(R.string.cost),
            value = String.format("%.2f €", service.cost)
        )

        if (service.note.isNotBlank()) {
            ServiceInfoRow(
                label = stringResource(R.string.note),
                value = service.note
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
private fun ServiceInfoRow(label: String, value: String) {
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
private fun ServiceEditForm(
    service: Service,
    modifier: Modifier = Modifier,
    onSave: (Service) -> Unit,
    onCancel: () -> Unit
) {
    var cost by remember { mutableStateOf(String.format("%.2f", service.cost)) }
    var note by remember { mutableStateOf(service.note) }
    var selectedTasks by remember { mutableStateOf(ServiceTask.fromBitmask(service.tasks)) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = service.date
    )

    var costError by remember { mutableStateOf(false) }
    var tasksError by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTaskPicker) {
        ServiceTaskPickerDialog(
            selectedTasks = selectedTasks,
            onConfirm = { tasks ->
                selectedTasks = tasks
                tasksError = false
                showTaskPicker = false
            },
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
            value = cost,
            onValueChange = { cost = it.filter { c -> c.isDigit() || c == '.' }; costError = false },
            label = { Text(stringResource(R.string.cost)) },
            isError = costError,
            supportingText = if (costError) { { Text(stringResource(R.string.field_required)) } } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = { Text("€") }
        )

        OutlinedTextField(
            value = datePickerState.selectedDateMillis?.let { millis ->
                val date = java.time.LocalDate.ofEpochDay(millis / 86400000)
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

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(stringResource(R.string.note)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

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

        if (tasksError) {
            Text(
                text = stringResource(R.string.tasks_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
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
                    costError = cost.isBlank()
                    tasksError = selectedTasks.isEmpty()

                    if (!costError && !tasksError) {
                        onSave(
                            service.copy(
                                cost = cost.toDouble(),
                                date = datePickerState.selectedDateMillis ?: service.date,
                                tasks = ServiceTask.toBitmask(selectedTasks),
                                note = note.trim()
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
