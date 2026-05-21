package me.matejkralovic.diacur.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.matejkralovic.diacur.data.dao.*
import me.matejkralovic.diacur.data.entity.*

@Database(
    entities = [
        Vehicle::class,
        Fueling::class,
        Service::class,
        Inspection::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = true
)
abstract class DiaCurDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelingDao(): FuelingDao
    abstract fun serviceDao(): ServiceDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: DiaCurDatabase? = null

        fun getDatabase(context: Context): DiaCurDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DiaCurDatabase::class.java,
                    "diacur_database"
                )
                    .fallbackToDestructiveMigration() // OK during development; replace with Migration before release
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

// Vytvorene pomocou AI

