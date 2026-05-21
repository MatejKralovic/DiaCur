package me.matejkralovic.diacur.ui.navigation

sealed class Screen(val route: String) {

    // ── Vehicle ──────────────────────────────────────────────
    data object VehicleList : Screen("vehicle_list")

    data object VehicleAdd : Screen("vehicle_add")

    data class VehicleDetail(val vehicleId: Long = 0) :
        Screen("vehicle_detail/{vehicleId}") {
        companion object {
            const val ROUTE = "vehicle_detail/{vehicleId}"
            fun createRoute(vehicleId: Long) = "vehicle_detail/$vehicleId"
        }
    }

    // ── Fueling ──────────────────────────────────────────────
    data class FuelingList(val vehicleId: Long = 0) :
        Screen("fueling_list/{vehicleId}") {
        companion object {
            const val ROUTE = "fueling_list/{vehicleId}"
            fun createRoute(vehicleId: Long) = "fueling_list/$vehicleId"
        }
    }

    data class FuelingAdd(val vehicleId: Long = 0) :
        Screen("fueling_add/{vehicleId}") {
        companion object {
            const val ROUTE = "fueling_add/{vehicleId}"
            fun createRoute(vehicleId: Long) = "fueling_add/$vehicleId"
        }
    }

    data class FuelingDetail(val fuelingId: Long = 0) :
        Screen("fueling_detail/{fuelingId}") {
        companion object {
            const val ROUTE = "fueling_detail/{fuelingId}"
            fun createRoute(fuelingId: Long) = "fueling_detail/$fuelingId"
        }
    }

    // ── Service ──────────────────────────────────────────────
    data class ServiceList(val vehicleId: Long = 0) :
        Screen("service_list/{vehicleId}") {
        companion object {
            const val ROUTE = "service_list/{vehicleId}"
            fun createRoute(vehicleId: Long) = "service_list/$vehicleId"
        }
    }

    data class ServiceAdd(val vehicleId: Long = 0) :
        Screen("service_add/{vehicleId}") {
        companion object {
            const val ROUTE = "service_add/{vehicleId}"
            fun createRoute(vehicleId: Long) = "service_add/$vehicleId"
        }
    }

    data class ServiceDetail(val serviceId: Long = 0) :
        Screen("service_detail/{serviceId}") {
        companion object {
            const val ROUTE = "service_detail/{serviceId}"
            fun createRoute(serviceId: Long) = "service_detail/$serviceId"
        }
    }

    // ── Inspection (STK/EK) ───────────────────────────────────
    data class InspectionList(val vehicleId: Long = 0) :
        Screen("inspection_list/{vehicleId}") {
        companion object {
            const val ROUTE = "inspection_list/{vehicleId}"
            fun createRoute(vehicleId: Long) = "inspection_list/$vehicleId"
        }
    }

    data class InspectionAdd(val vehicleId: Long = 0) :
        Screen("inspection_add/{vehicleId}") {
        companion object {
            const val ROUTE = "inspection_add/{vehicleId}"
            fun createRoute(vehicleId: Long) = "inspection_add/$vehicleId"
        }
    }

    data class InspectionDetail(val inspectionId: Long = 0) :
        Screen("inspection_detail/{inspectionId}") {
        companion object {
            const val ROUTE = "inspection_detail/{inspectionId}"
            fun createRoute(inspectionId: Long) = "inspection_detail/$inspectionId"
        }
    }

    // ── Reminder ─────────────────────────────────────────────
    data class ReminderList(val vehicleId: Long = 0) :
        Screen("reminder_list/{vehicleId}") {
        companion object {
            const val ROUTE = "reminder_list/{vehicleId}"
            fun createRoute(vehicleId: Long) = "reminder_list/$vehicleId"
        }
    }

    data class ReminderAdd(val vehicleId: Long = 0) :
        Screen("reminder_add/{vehicleId}") {
        companion object {
            const val ROUTE = "reminder_add/{vehicleId}"
            fun createRoute(vehicleId: Long) = "reminder_add/$vehicleId"
        }
    }

    data class ReminderDetail(val reminderId: Long = 0) :
        Screen("reminder_detail/{reminderId}") {
        companion object {
            const val ROUTE = "reminder_detail/{reminderId}"
            fun createRoute(reminderId: Long) = "reminder_detail/$reminderId"
        }
    }
}

// Vytvorene pomocou AI
