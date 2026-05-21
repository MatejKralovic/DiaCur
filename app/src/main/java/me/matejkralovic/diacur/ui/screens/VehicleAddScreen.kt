package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.VehicleType
import me.matejkralovic.diacur.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleAddScreen(
    navController: NavController,
    viewModel: VehicleViewModel
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VehicleType.CAR) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Field error states
    var nameError by remember { mutableStateOf(false) }
    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }
    var odometerError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_vehicle)) },
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

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text(stringResource(R.string.vehicle_name)) },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Brand
            OutlinedTextField(
                value = brand,
                onValueChange = {
                    brand = it
                    brandError = false
                },
                label = { Text(stringResource(R.string.vehicle_brand)) },
                isError = brandError,
                supportingText = if (brandError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Model
            OutlinedTextField(
                value = model,
                onValueChange = {
                    model = it
                    modelError = false
                },
                label = { Text(stringResource(R.string.vehicle_model)) },
                isError = modelError,
                supportingText = if (modelError) {
                    { Text(stringResource(R.string.field_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Odometer
            OutlinedTextField(
                value = odometer,
                onValueChange = {
                    odometer = it.filter { c -> c.isDigit() }
                    odometerError = false
                },
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

            // Vehicle type dropdown
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
                            onClick = {
                                selectedType = type
                                typeDropdownExpanded = false
                            },
                            leadingIcon = {
                                Icon(vehicleTypeIcon(type), contentDescription = null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            Button(
                onClick = {
                    // Validate
                    nameError = name.isBlank()
                    brandError = brand.isBlank()
                    modelError = model.isBlank()
                    odometerError = odometer.isBlank()

                    if (!nameError && !brandError && !modelError && !odometerError) {
                        viewModel.addVehicle(
                            name = name.trim(),
                            brand = brand.trim(),
                            model = model.trim(),
                            odometer = odometer.toInt(),
                            type = selectedType
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
