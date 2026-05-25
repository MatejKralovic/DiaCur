package me.matejkralovic.diacur.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.ui.viewmodel.AddFuelingResult
import me.matejkralovic.diacur.ui.viewmodel.FuelingViewModel
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelingAddScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: FuelingViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var totalPrice by rememberSaveable { mutableStateOf("") }
    var pricePerLitre by rememberSaveable { mutableStateOf("") }
    var volume by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var recordLocation by rememberSaveable { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var locationLoading by remember { mutableStateOf(false) }

    // Date picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Field errors
    var totalPriceError by remember { mutableStateOf(false) }
    var pricePerLitreError by remember { mutableStateOf(false) }
    var volumeError by remember { mutableStateOf(false) }
    var odometerError by remember { mutableStateOf(false) }
    var odometerTooLowError by remember { mutableStateOf<Int?>(null) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            locationLoading = true
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationToken = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                }
                locationLoading = false
            }.addOnFailureListener {
                locationLoading = false
            }
        } else {
            recordLocation = false
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_fueling)) },
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

            // Total price
            OutlinedTextField(
                value = totalPrice,
                onValueChange = {
                    totalPrice = it.filter { c -> c.isDigit() || c == '.' }
                    totalPriceError = false
                    // Auto-calculate price per litre if volume is filled
                    val p = totalPrice.toDoubleOrNull()
                    val v = volume.toDoubleOrNull()
                    if (p != null && v != null && v > 0) {
                        pricePerLitre = String.format("%.3f", p / v)
                    }
                },
                label = { Text(stringResource(R.string.fueling_total_price)) },
                isError = totalPriceError,
                supportingText = if (totalPriceError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("€") }
            )

            // Price per litre
            OutlinedTextField(
                value = pricePerLitre,
                onValueChange = {
                    pricePerLitre = it.filter { c -> c.isDigit() || c == '.' }
                    pricePerLitreError = false
                    // Auto-calculate total price if volume is filled
                    val p = pricePerLitre.toDoubleOrNull()
                    val v = volume.toDoubleOrNull()
                    if (p != null && v != null && v > 0) {
                        totalPrice = String.format("%.2f", p * v)
                    }
                },
                label = { Text(stringResource(R.string.fueling_price_per_litre)) },
                isError = pricePerLitreError,
                supportingText = if (pricePerLitreError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("€/l") }
            )

            // Volume
            OutlinedTextField(
                value = volume,
                onValueChange = {
                    volume = it.filter { c -> c.isDigit() || c == '.' }
                    volumeError = false
                    // Auto-calculate price per litre if total price is filled
                    val v = volume.toDoubleOrNull()
                    val p = totalPrice.toDoubleOrNull()
                    if (v != null && v > 0 && p != null) {
                        pricePerLitre = String.format("%.3f", p / v)
                    }
                },
                label = { Text(stringResource(R.string.fueling_volume)) },
                isError = volumeError,
                supportingText = if (volumeError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("l") }
            )

            // Odometer
            OutlinedTextField(
                value = odometer,
                onValueChange = {
                    odometer = it.filter { c -> c.isDigit() }
                    odometerError = false
                    odometerTooLowError = null
                },
                label = { Text(stringResource(R.string.vehicle_odometer)) },
                isError = odometerError || odometerTooLowError != null,
                supportingText = when {
                    odometerError -> { { Text(stringResource(R.string.field_required)) } }
                    odometerTooLowError != null -> { {
                        Text(stringResource(R.string.odometer_too_low, odometerTooLowError!!))
                    }}
                    else -> null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("km") }
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

            // Record location toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.record_location),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (latitude != null) {
                        Text(
                            text = stringResource(R.string.location_recorded),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (locationLoading) {
                        Text(
                            text = stringResource(R.string.location_loading),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Switch(
                    checked = recordLocation,
                    onCheckedChange = { checked ->
                        recordLocation = checked
                        if (checked) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                locationLoading = true
                                val fusedClient =
                                    LocationServices.getFusedLocationProviderClient(context)
                                val cancellationToken = CancellationTokenSource()
                                fusedClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY,
                                    cancellationToken.token
                                ).addOnSuccessListener { location ->
                                    location?.let {
                                        latitude = it.latitude
                                        longitude = it.longitude
                                    }
                                    locationLoading = false
                                }.addOnFailureListener {
                                    locationLoading = false
                                }
                            } else {
                                locationPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            }
                        } else {
                            latitude = null
                            longitude = null
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    totalPriceError = totalPrice.isBlank()
                    pricePerLitreError = pricePerLitre.isBlank()
                    volumeError = volume.isBlank()
                    odometerError = odometer.isBlank()

                    if (!totalPriceError && !pricePerLitreError && !volumeError && !odometerError) {
                        scope.launch {
                            val result = viewModel.addFueling(
                                vehicleId = vehicleId,
                                volume = volume.toDouble(),
                                pricePerLitre = pricePerLitre.toDouble(),
                                odometer = odometer.toInt(),
                                date = datePickerState.selectedDateMillis
                                    ?: System.currentTimeMillis(),
                                note = note.trim(),
                                latitude = if (recordLocation) latitude else null,
                                longitude = if (recordLocation) longitude else null
                            )
                            when (result) {
                                is AddFuelingResult.Success -> navController.popBackStack()
                                is AddFuelingResult.InvalidOdometer ->
                                    odometerTooLowError = result.lastOdometer
                            }
                        }
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
