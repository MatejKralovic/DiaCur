package me.matejkralovic.diacur.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.ui.navigation.Screen

// ── Bottom navigation bar ─────────────────────────────────────
@Composable
fun BottomBar(
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