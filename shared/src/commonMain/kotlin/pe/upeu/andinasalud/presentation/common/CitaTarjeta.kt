package pe.upeu.andinasalud.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Videocam
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

@Composable
fun CitaTarjeta(cita: CitaUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(cita.especialidad, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(cita.estado, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
            }
            Text(cita.medico, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    if (cita.modalidad == ModalidadAtencion.Teleconsulta) Icons.Default.Videocam
                    else Icons.Default.Place,
                    contentDescription = cita.modalidad.etiqueta,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(cita.modalidad.etiqueta, style = MaterialTheme.typography.bodySmall)
            }
            Text("${cita.fecha} · ${cita.hora}  |  ${cita.sede}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
