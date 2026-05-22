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
import me.matejkralovic.diacur.data.entity.Reminder
import me.matejkralovic.diacur.data.entity.ServiceTask
import me.matejkralovic.diacur.ui.navigation.Screen
import me.matejkralovic.diacur.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    navController: NavController,
    vehicleId: Long,
    viewModel: ReminderViewModel
) {
    val remindersFlow = remember(vehicleId) { viewModel.getRemindersForVehicle(vehicleId) }
    val reminders by remindersFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_reminders)) },
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
                navController.navigate(Screen.ReminderAdd.createRoute(vehicleId))
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_reminder))
            }
        },
        bottomBar = {
            DiaCurBottomBar(navController = navController, currentVehicleId = vehicleId)
        }
    ) { innerPadding ->
        if (reminders.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.no_reminders),
                hint = stringResource(R.string.no_reminders_hint),
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderListItem(
                        reminder = reminder,
                        onClick = {
                            navController.navigate(
                                Screen.ReminderDetail.createRoute(reminder.id)
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ── Reminder list item ────────────────────────────────────────
@Composable
private fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit
) {
    val tasks = remember(reminder.serviceTasks) {
        ServiceTask.fromBitmask(reminder.serviceTasks)
    }

    // Build trigger description
    val triggerText = buildString {
        reminder.kmTrigger?.let { append("$it km") }
        if (reminder.kmTrigger != null && reminder.dateTrigger != null) append("  ·  ")
        reminder.dateTrigger?.let {
            append(SimpleDateFormat("d. M. yyyy", Locale.getDefault()).format(Date(it)))
        }
    }

    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(reminder.description, fontWeight = FontWeight.Medium)
        },
        supportingContent = {
            if (triggerText.isNotBlank()) {
                Text(triggerText, color = MaterialTheme.colorScheme.outline)
            }
        },
        trailingContent = {
            if (tasks.isNotEmpty()) {
                Badge { Text("${tasks.size}") }
            }
        }
    )
}
// Vytvorene pomocou AI
