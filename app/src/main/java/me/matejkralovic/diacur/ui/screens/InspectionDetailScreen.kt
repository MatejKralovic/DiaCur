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
import me.matejkralovic.diacur.data.entity.Inspection
import me.matejkralovic.diacur.ui.viewmodel.InspectionViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    navController: NavController,
    inspectionId: Long,
    viewModel: InspectionViewModel
) {
    var inspection by remember { mutableStateOf<Inspection?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(inspectionId) {
        inspection = viewModel.getById(inspectionId)
        loaded = true
    }

    if (loaded && inspection == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    inspection?.let { ins ->
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.deleteInspection(ins)
                    navController.popBackStack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(ins.type.labelRes)) },
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
                InspectionEditForm(
                    inspection = ins,
                    modifier = Modifier.padding(innerPadding),
                    onSave = { updated ->
                        viewModel.updateInspection(updated)
                        editMode = false
                    },
                    onCancel = { editMode = false }
                )
            } else {
                InspectionInfo(
                    inspection = ins,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ── Inspection info (read-only) ───────────────────────────────
@Composable
private fun InspectionInfo(
    inspection: Inspection,
    modifier: Modifier = Modifier
) {
    val fmt = remember { SimpleDateFormat("d. M. yyyy", Locale.getDefault()) }
    val startFormatted = remember(inspection.startDate) { fmt.format(Date(inspection.startDate)) }
    val expiryFormatted = remember(inspection.expiryDate) { fmt.format(Date(inspection.expiryDate)) }
    val notificationFormatted = remember(inspection.notificationDate) {
        inspection.notificationDate?.let { fmt.format(Date(it)) }
    }
    val isExpired = inspection.expiryDate < System.currentTimeMillis()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InspectionInfoRow(
            label = stringResource(R.string.inspection_start_date),
            value = startFormatted
        )
        InspectionInfoRow(
            label = stringResource(R.string.inspection_expiry_date),
            value = expiryFormatted,
            valueColor = if (isExpired) MaterialTheme.colorScheme.error else null
        )
        InspectionInfoRow(
            label = stringResource(R.string.cost),
            value = String.format("%.2f €", inspection.cost)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.inspection_notify),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = if (inspection.notifyBeforeExpiry)
                    stringResource(R.string.yes)
                else
                    stringResource(R.string.no),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        if (inspection.notifyBeforeExpiry && notificationFormatted != null) {
            InspectionInfoRow(
                label = stringResource(R.string.inspection_notification_date),
                value = notificationFormatted
            )
        }
    }
}

@Composable
private fun InspectionInfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color? = null
) {
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
            fontWeight = FontWeight.Medium,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Edit form ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InspectionEditForm(
    inspection: Inspection,
    modifier: Modifier = Modifier,
    onSave: (Inspection) -> Unit,
    onCancel: () -> Unit
) {
    var cost by remember { mutableStateOf(String.format("%.2f", inspection.cost)) }
    var notifyBeforeExpiry by remember { mutableStateOf(inspection.notifyBeforeExpiry) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showExpiryDatePicker by remember { mutableStateOf(false) }
    var showNotificationDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = inspection.startDate
    )
    val expiryDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = inspection.expiryDate
    )
    val notificationDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = inspection.notificationDate ?: System.currentTimeMillis()
    )

    var costError by remember { mutableStateOf(false) }

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

        OutlinedTextField(
            value = expiryDatePickerState.selectedDateMillis?.let { millis ->
                val date = LocalDate.ofEpochDay(millis / 86400000)
                "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.inspection_expiry_date)) },
            trailingIcon = {
                IconButton(onClick = { showExpiryDatePicker = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.pick_date))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

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
                    if (!costError) {
                        onSave(
                            inspection.copy(
                                cost = cost.toDouble(),
                                startDate = startDatePickerState.selectedDateMillis
                                    ?: inspection.startDate,
                                expiryDate = expiryDatePickerState.selectedDateMillis
                                    ?: inspection.expiryDate,
                                notifyBeforeExpiry = notifyBeforeExpiry,
                                notificationDate = if (notifyBeforeExpiry)
                                    notificationDatePickerState.selectedDateMillis
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
