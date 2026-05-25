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
import me.matejkralovic.diacur.data.entity.InspectionType
import me.matejkralovic.diacur.ui.viewmodel.InspectionViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionAddScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: InspectionViewModel
) {
    var stkChecked by rememberSaveable { mutableStateOf(false) }
    var ekChecked by rememberSaveable { mutableStateOf(false) }
    var cost by rememberSaveable { mutableStateOf("") }
    var notifyBeforeExpiry by rememberSaveable { mutableStateOf(true) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showExpiryDatePicker by remember { mutableStateOf(false) }
    var showNotificationDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val expiryDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val notificationDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var typeError by remember { mutableStateOf(false) }
    var costError by remember { mutableStateOf(false) }
    var expiryDateError by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        ) { DatePicker(state = startDatePickerState) }
    }

    if (showExpiryDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showExpiryDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showExpiryDatePicker = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        ) { DatePicker(state = expiryDatePickerState) }
    }

    if (showNotificationDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showNotificationDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showNotificationDatePicker = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        ) { DatePicker(state = notificationDatePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_inspection)) },
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

            // STK / EK checkboxes
            Text(
                text = stringResource(R.string.inspection_type_label),
                style = MaterialTheme.typography.bodyMedium,
                color = if (typeError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline
            )
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = stkChecked,
                        onCheckedChange = { stkChecked = it; typeError = false }
                    )
                    Text(stringResource(R.string.inspection_type_stk))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = ekChecked,
                        onCheckedChange = { ekChecked = it; typeError = false }
                    )
                    Text(stringResource(R.string.inspection_type_ek))
                }
            }
            if (typeError) {
                Text(
                    text = stringResource(R.string.inspection_type_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

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

            // Start date
            OutlinedTextField(
                value = startDatePickerState.selectedDateMillis?.let { millis ->
                    val date = LocalDate.ofEpochDay(millis / 86400000)
                    "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.inspection_start_date)) },
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.pick_date))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Expiry date
            OutlinedTextField(
                value = expiryDatePickerState.selectedDateMillis?.let { millis ->
                    val date = LocalDate.ofEpochDay(millis / 86400000)
                    "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.inspection_expiry_date)) },
                isError = expiryDateError,
                supportingText = if (expiryDateError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                trailingIcon = {
                    IconButton(onClick = { showExpiryDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.pick_date))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Notify before expiry toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.inspection_notify),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = notifyBeforeExpiry,
                    onCheckedChange = { notifyBeforeExpiry = it }
                )
            }

            // Notification date (only visible when notify is on)
            if (notifyBeforeExpiry) {
                OutlinedTextField(
                    value = notificationDatePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / 86400000)
                        "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.inspection_notification_date)) },
                    trailingIcon = {
                        IconButton(onClick = { showNotificationDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.pick_date))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add button – creates one or two inspection records (STK + EK)
            Button(
                onClick = {
                    typeError = !stkChecked && !ekChecked
                    costError = cost.isBlank()
                    expiryDateError = expiryDatePickerState.selectedDateMillis == null

                    if (!typeError && !costError && !expiryDateError) {
                        val expiryMillis = expiryDatePickerState.selectedDateMillis!!
                        val startMillis = startDatePickerState.selectedDateMillis
                            ?: System.currentTimeMillis()
                        val notificationMillis = if (notifyBeforeExpiry)
                            notificationDatePickerState.selectedDateMillis
                        else null

                        if (stkChecked) {
                            viewModel.addInspection(
                                vehicleId = vehicleId,
                                type = InspectionType.STK,
                                cost = cost.toDouble(),
                                startDate = startMillis,
                                expiryDate = expiryMillis,
                                notifyBeforeExpiry = notifyBeforeExpiry,
                                notificationDate = notificationMillis
                            )
                        }
                        if (ekChecked) {
                            viewModel.addInspection(
                                vehicleId = vehicleId,
                                type = InspectionType.EK,
                                cost = cost.toDouble(),
                                startDate = startMillis,
                                expiryDate = expiryMillis,
                                notifyBeforeExpiry = notifyBeforeExpiry,
                                notificationDate = notificationMillis
                            )
                        }
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
