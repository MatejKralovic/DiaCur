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
import me.matejkralovic.diacur.data.entity.Inspection
import me.matejkralovic.diacur.data.entity.InspectionType
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.InspectionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionListScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: InspectionViewModel
) {
    val inspections by viewModel.getInspectionsForVehicle(vehicleId).collectAsState()

    // Separate STK and EK
    val stk = inspections.filter { it.type == InspectionType.STK }.maxByOrNull { it.expiryDate }
    val ek = inspections.filter { it.type == InspectionType.EK }.maxByOrNull { it.expiryDate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_inspection)) },
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
                navController.navigate(Screen.InspectionAdd.createRoute(vehicleId))
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_inspection))
            }
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = vehicleId)
        }
    ) { innerPadding ->
        if (inspections.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.no_inspections),
                hint = stringResource(R.string.no_inspections_hint),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Show latest STK
                stk?.let { inspection ->
                    item {
                        InspectionListItem(
                            inspection = inspection,
                            onClick = {
                                navController.navigate(
                                    Screen.InspectionDetail.createRoute(inspection.id)
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }

                // Show latest EK
                ek?.let { inspection ->
                    item {
                        InspectionListItem(
                            inspection = inspection,
                            onClick = {
                                navController.navigate(
                                    Screen.InspectionDetail.createRoute(inspection.id)
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }

                // Show history of older inspections
                val history = inspections.filter {
                    it.id != stk?.id && it.id != ek?.id
                }.sortedByDescending { it.expiryDate }

                if (history.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.inspection_history),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(history, key = { it.id }) { inspection ->
                        InspectionListItem(
                            inspection = inspection,
                            onClick = {
                                navController.navigate(
                                    Screen.InspectionDetail.createRoute(inspection.id)
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

// ── Inspection list item ──────────────────────────────────────
@Composable
private fun InspectionListItem(
    inspection: Inspection,
    onClick: () -> Unit
) {
    val expiryFormatted = remember(inspection.expiryDate) {
        SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(inspection.expiryDate))
    }
    val isExpired = inspection.expiryDate < System.currentTimeMillis()
    val isExpiringSoon = !isExpired &&
            inspection.expiryDate - System.currentTimeMillis() < 30L * 24 * 60 * 60 * 1000 // 30 days

    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = stringResource(inspection.type.labelRes),
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.inspection_valid_until, expiryFormatted),
                color = when {
                    isExpired -> MaterialTheme.colorScheme.error
                    isExpiringSoon -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        },
        trailingContent = {
            when {
                isExpired -> Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(stringResource(R.string.inspection_expired))
                }
                isExpiringSoon -> Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Text(stringResource(R.string.inspection_expiring_soon))
                }
            }
        }
    )
}
// Vytvorene pomocou AI
