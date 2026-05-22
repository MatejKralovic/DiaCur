package me.matejkralovic.diacur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import me.matejkralovic.diacur.notifications.ReminderWorker
import me.matejkralovic.diacur.ui.navigation.NavGraph
import me.matejkralovic.diacur.ui.theme.DiaCurTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaCurTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }

        // Testovanie notifikacii
         val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>().build()
         WorkManager.getInstance(this).enqueue(workRequest)
    }
}
