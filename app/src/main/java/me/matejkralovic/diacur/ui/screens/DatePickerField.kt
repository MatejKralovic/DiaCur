package me.matejkralovic.diacur.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.matejkralovic.diacur.R
import java.time.LocalDate

@Composable
fun DatePickerField(
    dateMillis: Long?,
    label: String,
    modifier: Modifier = Modifier,
    onPickerClick: () -> Unit
) {
    val dateText = dateMillis?.let { millis ->
        val date = LocalDate.ofEpochDay(millis / 86400000)
        "${date.dayOfMonth}. ${date.monthValue}. ${date.year}"
    } ?: ""

    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = onPickerClick) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.pick_date)
                )
            }
        },
        modifier = modifier
    )
}
// Vytvorene pomocou AI