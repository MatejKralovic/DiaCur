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
import kotlinx.coroutines.launch
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Vehicle
import me.matejkralovic.diacur.data.entity.VehicleType
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.FuelingViewModel
import me.matejkralovic.diacur.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    navController: NavController,
    vehicleId: Long,
    vehicleViewModel: VehicleViewModel,
    fuelingViewModel: FuelingViewModel
) {
    var isDeleting by remember { mutableStateOf(false) }

    val vehicles by vehicleViewModel.vehicles.collectAsState()
    val vehicle = vehicles.find { it.id == vehicleId }

    if (vehicle == null) {
        if (!isDeleting) {
            LaunchedEffect(Unit) { navController.popBackStack() }
        }
        return
    }

    var totalCost by remember { mutableStateOf(0.0) }
    var avgConsumption by remember { mutableStateOf<Double?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicleId) {
        totalCost = vehicleViewModel.getTotalCost(vehicleId)
        avgConsumption = fuelingViewModel.getAvgConsumption(vehicleId)
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = {
                isDeleting = true
                vehicleViewModel.deleteVehicle(vehicle)
                navController.navigate(Screen.VehicleList.route) {
                    popUpTo(Screen.VehicleList.route) { inclusive = true }
                }
            },
            onDismiss = { showDeleteDialog = false },
            title = stringResource(R.string.vehicle_delete_confirm_title),
            message = stringResource(R.string.vehicle_delete_confirm_message)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle.name) },
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
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = vehicleId)
        }
    ) { innerPadding ->
        if (editMode) {
            VehicleEditForm(
                vehicle = vehicle,
                modifier = Modifier.padding(innerPadding),
                onSave = { updated ->
                    vehicleViewModel.updateVehicle(updated)
                    editMode = false
                },
                onCancel = { editMode = false }
            )
        } else {
            VehicleInfo(
                vehicle = vehicle,
                totalCost = totalCost,
                avgConsumption = avgConsumption,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// ── Vehicle info (read-only) ──────────────────────────────────
@Composable
private fun VehicleInfo(
    vehicle: Vehicle,
    totalCost: Double,
    avgConsumption: Double?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Vehicle icon + type
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = vehicleTypeIcon(vehicle.type),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(vehicle.type.labelRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        VehicleInfoRow(label = stringResource(R.string.vehicle_brand), value = vehicle.brand)
        VehicleInfoRow(label = stringResource(R.string.vehicle_model), value = vehicle.model)
        VehicleInfoRow(
            label = stringResource(R.string.vehicle_odometer),
            value = "${vehicle.odometer} km"
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Calculated stats
        VehicleInfoRow(
            label = stringResource(R.string.avg_consumption),
            value = if (avgConsumption != null)
                String.format("%.1f l/100km", avgConsumption)
            else
                stringResource(R.string.not_available)
        )
        VehicleInfoRow(
            label = stringResource(R.string.total_cost),
            value = String.format("%.2f €", totalCost)
        )
    }
}

@Composable
private fun VehicleInfoRow(label: String, value: String) {
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
private fun VehicleEditForm(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
    onSave: (Vehicle) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(vehicle.name) }
    var brand by remember { mutableStateOf(vehicle.brand) }
    var model by remember { mutableStateOf(vehicle.model) }
    var odometer by remember { mutableStateOf(vehicle.odometer.toString()) }
    var selectedType by remember { mutableStateOf(vehicle.type) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }
    var odometerError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = { Text(stringResource(R.string.vehicle_name)) },
            isError = nameError,
            supportingText = if (nameError) {
                { Text(stringResource(R.string.field_required)) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it; brandError = false },
            label = { Text(stringResource(R.string.vehicle_brand)) },
            isError = brandError,
            supportingText = if (brandError) {
                { Text(stringResource(R.string.field_required)) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = model,
            onValueChange = { model = it; modelError = false },
            label = { Text(stringResource(R.string.vehicle_model)) },
            isError = modelError,
            supportingText = if (modelError) {
                { Text(stringResource(R.string.field_required)) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = odometer,
            onValueChange = { odometer = it.filter { c -> c.isDigit() }; odometerError = false },
            label = { Text(stringResource(R.string.vehicle_odometer)) },
            isError = odometerError,
            supportingText = if (odometerError) {
                { Text(stringResource(R.string.field_required)) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text("km") }
        )

        ExposedDropdownMenuBox(
            expanded = typeDropdownExpanded,
            onExpandedChange = { typeDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = stringResource(selectedType.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.vehicle_type)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }
            ) {
                VehicleType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(stringResource(type.labelRes)) },
                        onClick = { selectedType = type; typeDropdownExpanded = false },
                        leadingIcon = {
                            Icon(vehicleTypeIcon(type), contentDescription = null)
                        }
                    )
                }
            }
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
                    nameError = name.isBlank()
                    brandError = brand.isBlank()
                    modelError = model.isBlank()
                    odometerError = odometer.isBlank()

                    if (!nameError && !brandError && !modelError && !odometerError) {
                        onSave(
                            vehicle.copy(
                                name = name.trim(),
                                brand = brand.trim(),
                                model = model.trim(),
                                odometer = odometer.toInt(),
                                type = selectedType
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

// ── Delete confirmation dialog ────────────────────────────────
@Composable
fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(R.string.delete_confirm_title),
    message: String = stringResource(R.string.delete_confirm_message)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
// Vytvorene pomocou AI
// Chyby:
// Po zmazani vozidla zostala obrazovka biela
// -- Pridanie isDeleting
//
// Pri mazani zaznamov patriacich vozidlu sa vyuziva DeleteConfirmDialog z tohto suboru,
// takze title a message boli zle.
// -- Pridanie title a message ako parametre funkcie