package me.matejkralovic.diacur.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.matejkralovic.diacur.DiaCurApp
import me.matejkralovic.diacur.ui.navigation.Screen.FuelingDetail
import me.matejkralovic.diacur.ui.navigation.Screen.FuelingList
import me.matejkralovic.diacur.ui.navigation.Screen.InspectionDetail
import me.matejkralovic.diacur.ui.navigation.Screen.InspectionList
import me.matejkralovic.diacur.ui.navigation.Screen.ReminderDetail
import me.matejkralovic.diacur.ui.navigation.Screen.ReminderList
import me.matejkralovic.diacur.ui.navigation.Screen.ServiceDetail
import me.matejkralovic.diacur.ui.navigation.Screen.ServiceList
import me.matejkralovic.diacur.ui.navigation.Screen.VehicleDetail
import me.matejkralovic.diacur.ui.screens.*
import me.matejkralovic.diacur.ui.viewmodel.*

@Composable
fun NavGraph(navController: NavHostController) {
    val app = LocalContext.current.applicationContext as DiaCurApp

    // ViewModels
    val vehicleViewModel: VehicleViewModel = viewModel(
        factory = VehicleViewModel.factory(
            app.vehicleRepository,
            app.fuelingRepository,
            app.serviceRepository
        )
    )
    val fuelingViewModel: FuelingViewModel = viewModel(
        factory = FuelingViewModel.factory(
            app.fuelingRepository,
            app.vehicleRepository
        )
    )
    val serviceViewModel: ServiceViewModel = viewModel(
        factory = ServiceViewModel.factory(app.serviceRepository)
    )
    val inspectionViewModel: InspectionViewModel = viewModel(
        factory = InspectionViewModel.factory(app.inspectionRepository)
    )
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModel.factory(app.reminderRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.VehicleList.route
    ) {

        // ── Vehicle ──────────────────────────────────────────
        composable(Screen.VehicleList.route) {
            VehicleListScreen(
                navController = navController,
                viewModel = vehicleViewModel
            )
        }

        composable(Screen.VehicleAdd.route) {
            VehicleAddScreen(
                navController = navController,
                viewModel = vehicleViewModel
            )
        }

        composable(
            route = VehicleDetail.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            VehicleDetailScreen(
                navController = navController,
                vehicleId = vehicleId,
                vehicleViewModel = vehicleViewModel,
                fuelingViewModel = fuelingViewModel
            )
        }

        // ── Fueling ──────────────────────────────────────────
        composable(
            route = FuelingList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            FuelingListScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = fuelingViewModel
            )
        }

        composable(
            route = Screen.FuelingAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            FuelingAddScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = fuelingViewModel
            )
        }

        composable(
            route = FuelingDetail.ROUTE,
            arguments = listOf(navArgument("fuelingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val fuelingId = backStackEntry.arguments?.getLong("fuelingId") ?: return@composable
            FuelingDetailScreen(
                navController = navController,
                fuelingId = fuelingId,
                viewModel = fuelingViewModel
            )
        }

        // ── Service ──────────────────────────────────────────
        composable(
            route = ServiceList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            ServiceListScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = serviceViewModel
            )
        }

        composable(
            route = Screen.ServiceAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            ServiceAddScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = serviceViewModel
            )
        }

        composable(
            route = ServiceDetail.ROUTE,
            arguments = listOf(navArgument("serviceId") { type = NavType.LongType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getLong("serviceId") ?: return@composable
            ServiceDetailScreen(
                navController = navController,
                serviceId = serviceId,
                viewModel = serviceViewModel
            )
        }

        // ── Inspection ────────────────────────────────────────
        composable(
            route = InspectionList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            InspectionListScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = inspectionViewModel
            )
        }

        composable(
            route = Screen.InspectionAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            InspectionAddScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = inspectionViewModel
            )
        }

        composable(
            route = InspectionDetail.ROUTE,
            arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getLong("inspectionId") ?: return@composable
            InspectionDetailScreen(
                navController = navController,
                inspectionId = inspectionId,
                viewModel = inspectionViewModel
            )
        }

        // ── Reminder ─────────────────────────────────────────
        composable(
            route = ReminderList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            ReminderListScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = reminderViewModel
            )
        }

        composable(
            route = Screen.ReminderAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            ReminderAddScreen(
                navController = navController,
                vehicleId = vehicleId,
                viewModel = reminderViewModel
            )
        }

        composable(
            route = ReminderDetail.ROUTE,
            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId") ?: return@composable
            ReminderDetailScreen(
                navController = navController,
                reminderId = reminderId,
                viewModel = reminderViewModel
            )
        }
    }
}

// Vytvorene pomocou AI
