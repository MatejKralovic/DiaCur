package me.matejkralovic.diacur.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.Fueling
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.FuelingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelingListScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: FuelingViewModel
) {
    val fuelingsFlow = remember(vehicleId) { viewModel.getFuelingsForVehicle(vehicleId) }
    val fuelings by fuelingsFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_fueling)) },
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
                navController.navigate(Screen.FuelingAdd.createRoute(vehicleId))
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_fueling))
            }
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = vehicleId)
        }
    ) { innerPadding ->
        if (fuelings.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.no_fuelings),
                hint = stringResource(R.string.no_fuelings_hint),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(fuelings, key = { it.id }) { fueling ->
                    FuelingListItem(
                        fueling = fueling,
                        onClick = {
                            navController.navigate(
                                Screen.FuelingDetail.createRoute(fueling.id)
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ── Fueling list item ─────────────────────────────────────────
@Composable
private fun FuelingListItem(
    fueling: Fueling,
    onClick: () -> Unit
) {
    val totalPrice = fueling.volume * fueling.pricePerLitre
    val dateFormatted = remember(fueling.date) {
        SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(fueling.date))
    }

    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = String.format("%.1f l  –  %.2f €", fueling.volume, totalPrice),
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(dateFormatted)
        },
        trailingContent = {
            if (fueling.latitude != null) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.location_saved),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
}

// ── Reusable empty state ──────────────────────────────────────
@Composable
fun EmptyState(
    message: String,
    hint: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// Vytvorene pomocou AI
