package me.matejkralovic.diacur.data.entity

import me.matejkralovic.diacur.R
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val cost: Double,
    val date: Long,
    val tasks: Int,
    val note: String = ""
)

enum class ServiceTask(val bit: Int, val labelRes: Int) {
    OIL_CHANGE      (1, R.string.service_task_oil),
    TYRE_CHANGE       (2,   R.string.service_task_tyres),
    BRAKE_SERVICE   (4,   R.string.service_task_brake_service),
    TIMING_BELTS    (8,   R.string.service_task_timing_belts),
    SPARK_PLUGS     (16,  R.string.service_task_spark_plugs),
    BULBS           (32,  R.string.service_task_bulbs),
    COOLANT         (64,  R.string.service_task_coolant),
    WHEEL_ALIGNMENT (128, R.string.service_task_wheel_alignment),
    OTHER           (256, R.string.service_task_other);

    companion object {
        fun fromBitmask(mask: Int): Set<ServiceTask> =
            entries.filter { mask and it.bit != 0 }.toSet()

        fun toBitmask(tasks: Set<ServiceTask>): Int =
            tasks.fold(0) { acc, task -> acc or task.bit }
    }
}
// Vytvorene pomocou AI
// Uprava enumu - Verzia od Claude mala pevne nastaveny 2. parameter ako String nazov
// upravil som to tak, aby sa vyuzival resource
