package me.matejkralovic.diacur.ui.screens

import ServiceTaskPickerDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceAddScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: ServiceViewModel
) {
    var cost by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var selectedTasks by rememberSaveable { mutableStateOf(emptySet<ServiceTask>()) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_service)) },
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

            // Cost
            OutlinedTextField(
                value = cost,
                onValueChange = {
                    cost = it.filter { c -> c.isDigit() || c == '.' }
                    costError = false
                },
                label = { Text(stringResource(R.string.cost)) },
                isError = costError,
                supportingText = if (costError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("€") }
            )

            // Date
            DatePickerField(
                dateMillis = datePickerState.selectedDateMillis,
                label = stringResource(R.string.date),
                modifier = Modifier.fillMaxWidth(),
                onPickerClick = { showDatePicker = true }
            )

            // Note
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

            // Pick tasks button
            OutlinedButton(
                onClick = { showTaskPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_tasks))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            Button(
                onClick = {
                    costError = cost.isBlank()
                    tasksError = selectedTasks.isEmpty()

                    if (!costError && !tasksError) {
                        viewModel.addService(
                            vehicleId = vehicleId,
                            cost = cost.toDouble(),
                            date = datePickerState.selectedDateMillis
                                ?: System.currentTimeMillis(),
                            tasks = selectedTasks,
                            note = note.trim()
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
