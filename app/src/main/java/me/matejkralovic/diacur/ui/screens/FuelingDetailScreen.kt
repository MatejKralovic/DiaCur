package me.matejkralovic.diacur.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Fueling
import me.matejkralovic.diacur.ui.viewmodel.FuelingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelingDetailScreen(
    navController: NavController,
    fuelingId: Long,
    viewModel: FuelingViewModel
) {
    // Find fueling from all vehicles' flows – simplest approach for detail screen
    var fueling by remember { mutableStateOf<Fueling?>(null) }
    var loaded by remember { mutableStateOf(false) }

    // We collect all fuelings and find by id
    // In a real app you'd add getById to the ViewModel; this works for now
    LaunchedEffect(fuelingId) {
        fueling = viewModel.getById(fuelingId)
        loaded = true
    }

    var editMode by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Until fueling is loaded navigate back
    if (loaded && fueling == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    fueling?.let { f ->
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.deleteFueling(f)
                    navController.popBackStack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.fueling_detail_title)) },
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
                FuelingEditForm(
                    fueling = f,
                    modifier = Modifier.padding(innerPadding),
                    onSave = { updated ->
                        viewModel.updateFueling(updated)
                        editMode = false
                    },
                    onCancel = { editMode = false }
                )
            } else {
                FuelingInfo(
                    fueling = f,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ── Fueling info (read-only) ──────────────────────────────────
@Composable
private fun FuelingInfo(
    fueling: Fueling,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormatted = remember(fueling.date) {
        SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(fueling.date))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FuelingInfoRow(
            label = stringResource(R.string.fueling_volume),
            value = String.format("%.2f l", fueling.volume)
        )
        FuelingInfoRow(
            label = stringResource(R.string.fueling_price_per_litre),
            value = String.format("%.3f €/l", fueling.pricePerLitre)
        )
        FuelingInfoRow(
            label = stringResource(R.string.fueling_total_price),
            value = String.format("%.2f €", fueling.volume * fueling.pricePerLitre)
        )
        FuelingInfoRow(
            label = stringResource(R.string.vehicle_odometer),
            value = "${fueling.odometer} km"
        )
        FuelingInfoRow(
            label = stringResource(R.string.date),
            value = dateFormatted
        )
        if (fueling.note.isNotBlank()) {
            FuelingInfoRow(
                label = stringResource(R.string.note),
                value = fueling.note
            )
        }

        // Location button
        if (fueling.latitude != null && fueling.longitude != null) {
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(
                onClick = {
                    val uri = Uri.parse("geo:${fueling.latitude},${fueling.longitude}?q=${fueling.latitude},${fueling.longitude}")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.show_on_map))
            }
        }
    }
}

@Composable
private fun FuelingInfoRow(label: String, value: String) {
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ── Edit form ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelingEditForm(
    fueling: Fueling,
    modifier: Modifier = Modifier,
    onSave: (Fueling) -> Unit,
    onCancel: () -> Unit
) {
    var totalPrice by rememberSaveable { mutableStateOf(String.format("%.2f", fueling.volume * fueling.pricePerLitre)) }
    var pricePerLitre by rememberSaveable { mutableStateOf(String.format("%.3f", fueling.pricePerLitre)) }
    var volume by rememberSaveable { mutableStateOf(String.format("%.2f", fueling.volume)) }
    var odometer by rememberSaveable { mutableStateOf(fueling.odometer.toString()) }
    var note by rememberSaveable { mutableStateOf(fueling.note) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fueling.date
    )

    var totalPriceError by remember { mutableStateOf(false) }
    var pricePerLitreError by remember { mutableStateOf(false) }
    var volumeError by remember { mutableStateOf(false) }
    var odometerError by remember { mutableStateOf(false) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = totalPrice,
            onValueChange = {
                totalPrice = it.filter { c -> c.isDigit() || c == '.' }
                totalPriceError = false
                val p = totalPrice.toDoubleOrNull()
                val v = volume.toDoubleOrNull()
                if (p != null && v != null && v > 0) {
                    pricePerLitre = String.format("%.3f", p / v)
                }
            },
            label = { Text(stringResource(R.string.fueling_total_price)) },
            isError = totalPriceError,
            supportingText = if (totalPriceError) { { Text(stringResource(R.string.field_required)) } } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = { Text("€") }
        )

        OutlinedTextField(
            value = pricePerLitre,
            onValueChange = {
                pricePerLitre = it.filter { c -> c.isDigit() || c == '.' }
                pricePerLitreError = false
                val p = pricePerLitre.toDoubleOrNull()
                val v = volume.toDoubleOrNull()
                if (p != null && v != null && v > 0) {
                    totalPrice = String.format("%.2f", p * v)
                }
            },
            label = { Text(stringResource(R.string.fueling_price_per_litre)) },
            isError = pricePerLitreError,
            supportingText = if (pricePerLitreError) { { Text(stringResource(R.string.field_required)) } } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = { Text("€/l") }
        )

        OutlinedTextField(
            value = volume,
            onValueChange = {
                volume = it.filter { c -> c.isDigit() || c == '.' }
                volumeError = false
                val v = volume.toDoubleOrNull()
                val p = totalPrice.toDoubleOrNull()
                if (v != null && v > 0 && p != null) {
                    pricePerLitre = String.format("%.3f", p / v)
                }
            },
            label = { Text(stringResource(R.string.fueling_volume)) },
            isError = volumeError,
            supportingText = if (volumeError) { { Text(stringResource(R.string.field_required)) } } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = { Text("l") }
        )

        OutlinedTextField(
            value = odometer,
            onValueChange = { odometer = it.filter { c -> c.isDigit() }; odometerError = false },
            label = { Text(stringResource(R.string.vehicle_odometer)) },
            isError = odometerError,
            supportingText = if (odometerError) { { Text(stringResource(R.string.field_required)) } } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text("km") }
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
                    totalPriceError = totalPrice.isBlank()
                    pricePerLitreError = pricePerLitre.isBlank()
                    volumeError = volume.isBlank()
                    odometerError = odometer.isBlank()

                    if (!totalPriceError && !pricePerLitreError && !volumeError && !odometerError) {
                        onSave(
                            fueling.copy(
                                volume = volume.toDouble(),
                                pricePerLitre = pricePerLitre.toDouble(),
                                odometer = odometer.toInt(),
                                date = datePickerState.selectedDateMillis ?: fueling.date,
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
