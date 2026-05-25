import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.matejkralovic.diacur.R
import me.matejkralovic.diacur.data.entity.ServiceTask

@Composable
fun ServiceTaskPickerDialog(
    selectedTasks: Set<ServiceTask>,
    onConfirm: (Set<ServiceTask>) -> Unit,
    onDismiss: () -> Unit
) {
    var current by remember { mutableStateOf(selectedTasks) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pick_tasks)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                ServiceTask.entries.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(task.labelRes),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                        )
                        Switch(
                            checked = task in current,
                            onCheckedChange = { checked ->
                                current = if (checked) current + task else current - task
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(current) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
// Vytvorene pomocou AI