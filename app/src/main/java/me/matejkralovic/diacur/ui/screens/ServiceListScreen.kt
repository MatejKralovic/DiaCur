package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Service
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.ServiceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: ServiceViewModel
) {
    val servicesFlow = remember(vehicleId) { viewModel.getServicesForVehicle(vehicleId) }
    val services by servicesFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_service)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.ServiceAdd.createRoute(vehicleId))
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_service))
            }
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = vehicleId)
        }
    ) { innerPadding ->
        if (services.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.no_services),
                hint = stringResource(R.string.no_services_hint),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(services, key = { it.id }) { service ->
                    ServiceListItem(
                        service = service,
                        onClick = {
                            navController.navigate(
                                Screen.ServiceDetail.createRoute(service.id)
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ── Service list item ─────────────────────────────────────────
@Composable
private fun ServiceListItem(
    service: Service,
    onClick: () -> Unit
) {
    val dateFormatted = remember(service.date) {
        SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(service.date))
    }
    val tasks = remember(service.tasks) {
        ServiceTask.fromBitmask(service.tasks)
    }
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = if (tasks.isNotEmpty())
                    stringResource(tasks.first().labelRes)
                else
                    stringResource(R.string.service_no_tasks),
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text("$dateFormatted  ·  ${String.format("%.2f €", service.cost)}")
        },
        trailingContent = {
            if (tasks.size > 1) {
                Badge { Text("+${tasks.size - 1}") }
            }
        }
    )
}
// Vytvorene pomocou AI
