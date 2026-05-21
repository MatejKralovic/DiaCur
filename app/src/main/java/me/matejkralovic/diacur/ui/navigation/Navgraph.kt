package me.matejkralovic.diacur.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun DiaCurNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.VehicleList.route
    ) {

        // ── Vehicle ──────────────────────────────────────────
        composable(Screen.VehicleList.route) {
            // VehicleListScreen(navController)
        }

        composable(Screen.VehicleAdd.route) {
            // VehicleAddScreen(navController)
        }

        composable(
            route = Screen.VehicleDetail.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // VehicleDetailScreen(navController, vehicleId)
        }

        // ── Fueling ──────────────────────────────────────────
        composable(
            route = Screen.FuelingList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // FuelingListScreen(navController, vehicleId)
        }

        composable(
            route = Screen.FuelingAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // FuelingAddScreen(navController, vehicleId)
        }

        composable(
            route = Screen.FuelingDetail.ROUTE,
            arguments = listOf(navArgument("fuelingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val fuelingId = backStackEntry.arguments?.getLong("fuelingId") ?: return@composable
            // FuelingDetailScreen(navController, fuelingId)
        }

        // ── Service ──────────────────────────────────────────
        composable(
            route = Screen.ServiceList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // ServiceListScreen(navController, vehicleId)
        }

        composable(
            route = Screen.ServiceAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // ServiceAddScreen(navController, vehicleId)
        }

        composable(
            route = Screen.ServiceDetail.ROUTE,
            arguments = listOf(navArgument("serviceId") { type = NavType.LongType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getLong("serviceId") ?: return@composable
            // ServiceDetailScreen(navController, serviceId)
        }

        // ── Inspection ────────────────────────────────────────
        composable(
            route = Screen.InspectionList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // InspectionListScreen(navController, vehicleId)
        }

        composable(
            route = Screen.InspectionAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // InspectionAddScreen(navController, vehicleId)
        }

        composable(
            route = Screen.InspectionDetail.ROUTE,
            arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getLong("inspectionId") ?: return@composable
            // InspectionDetailScreen(navController, inspectionId)
        }

        // ── Reminder ─────────────────────────────────────────
        composable(
            route = Screen.ReminderList.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // ReminderListScreen(navController, vehicleId)
        }

        composable(
            route = Screen.ReminderAdd.ROUTE,
            arguments = listOf(navArgument("vehicleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getLong("vehicleId") ?: return@composable
            // ReminderAddScreen(navController, vehicleId)
        }

        composable(
            route = Screen.ReminderDetail.ROUTE,
            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId") ?: return@composable
            // ReminderDetailScreen(navController, reminderId)
        }
    }
}

// Vytvorene pomocou AI
