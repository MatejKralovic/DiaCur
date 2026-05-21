package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Vehicle
import me.matejkralovic.diacur.data.entity.VehicleType
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.VehicleViewModel

@Composable
fun VehicleListScreen(
    navController: NavController,
    viewModel: VehicleViewModel
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.VehicleAdd.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_vehicle))
            }
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = null)
        }
    ) { innerPadding ->
        if (vehicles.isEmpty()) {
            EmptyVehicleList(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(vehicles, key = { it.id }) { vehicle ->
                    VehicleListItem(
                        vehicle = vehicle,
                        onClick = {
                            navController.navigate(
                                Screen.VehicleDetail.createRoute(vehicle.id)
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ── Vehicle list item ─────────────────────────────────────────
@Composable
private fun VehicleListItem(
    vehicle: Vehicle,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(vehicle.name, fontWeight = FontWeight.Medium)
        },
        supportingContent = {
            Text("${vehicle.brand} ${vehicle.model}")
        },
        trailingContent = {
            Icon(
                imageVector = vehicleTypeIcon(vehicle.type),
                contentDescription = stringResource(vehicle.type.labelRes)
            )
        }
    )
}

// ── Empty state ───────────────────────────────────────────────
@Composable
private fun EmptyVehicleList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_vehicles),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_vehicles_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// ── Bottom navigation bar ─────────────────────────────────────
@Composable
fun DiaCurBottomBar(
    navController: NavController,
    currentVehicleId: Long?
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Screen.VehicleList.route) {
                    popUpTo(Screen.VehicleList.route) { inclusive = true }
                }
            },
            icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_vehicles)) }
        )
        NavigationBarItem(
            selected = false,
            enabled = currentVehicleId != null,
            onClick = {
                currentVehicleId?.let {
                    navController.navigate(Screen.FuelingList.createRoute(it))
                }
            },
            icon = { Icon(Icons.Default.LocalGasStation, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_fueling)) }
        )
        NavigationBarItem(
            selected = false,
            enabled = currentVehicleId != null,
            onClick = {
                currentVehicleId?.let {
                    navController.navigate(Screen.ServiceList.createRoute(it))
                }
            },
            icon = { Icon(Icons.Default.Build, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_service)) }
        )
        NavigationBarItem(
            selected = false,
            enabled = currentVehicleId != null,
            onClick = {
                currentVehicleId?.let {
                    navController.navigate(Screen.ReminderList.createRoute(it))
                }
            },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_reminders)) }
        )
        NavigationBarItem(
            selected = false,
            enabled = currentVehicleId != null,
            onClick = {
                currentVehicleId?.let {
                    navController.navigate(Screen.InspectionList.createRoute(it))
                }
            },
            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_inspection)) }
        )
    }
}

// ── Helper – icon for vehicle type ───────────────────────────
fun vehicleTypeIcon(type: VehicleType) = when (type) {
    VehicleType.CAR        -> Icons.Default.DirectionsCar
    VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
    VehicleType.VAN        -> Icons.Default.LocalShipping
    VehicleType.OTHER      -> Icons.Default.DirectionsCar
}
// Vytvorene pomocou AI
