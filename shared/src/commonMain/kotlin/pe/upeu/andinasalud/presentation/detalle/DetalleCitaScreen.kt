package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.EstadoPanel

@Composable
fun DetalleCitaScreen(
    citaId: Long,
    viewModel: DetalleCitaViewModel,
    modifier: Modifier = Modifier,
) {
    val fase by viewModel.fase.collectAsState()
    var confirmarCancelacion by remember { mutableStateOf(false) }
    LaunchedEffect(citaId) { viewModel.abrir(citaId) }

    when (val actual = fase) {
        FaseDetalle.Cargando -> EstadoPanel("Cargando detalle…", "Consultando la cita", cargando = true,
            modifier = modifier)
        FaseDetalle.Vacia -> EstadoPanel("Cita no encontrada", "La cita ya no está disponible",
            modifier = modifier)
        is FaseDetalle.Error -> EstadoPanel("Error de carga", actual.mensaje,
            error = true, reintentar = viewModel::cargar, modifier = modifier)
        is FaseDetalle.Contenido -> {
            Column(
                modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(actual.cita.especialidad, style = MaterialTheme.typography.headlineSmall)
                Text(actual.cita.estado, color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatoDetalle("Médico", actual.cita.medico)
                        DatoDetalle("Sede", actual.cita.sede)
                        DatoDetalle("Fecha", actual.cita.fecha)
                        DatoDetalle("Hora", actual.cita.hora)
                        DatoDetalle("Motivo", actual.cita.motivo)
                        actual.cita.indicaciones?.let {
                            DatoDetalle(
                                if (actual.cita.estado == "Cancelada") "Motivo de cancelación" else "Indicaciones",
                                it,
                            )
                        }
                    }
                }
                if (actual.puedeCancelar) {
                    OutlinedButton(
                        onClick = { confirmarCancelacion = true },
                        enabled = !actual.cancelando,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(if (actual.cancelando) "Cancelando…" else "Cancelar cita") }
                } else if (actual.cita.estado == "Programada") {
                    Text("La cancelación requiere más de 24 horas de anticipación",
                        style = MaterialTheme.typography.bodySmall)
                }
                actual.errorAccion?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (confirmarCancelacion) {
        AlertDialog(
            onDismissRequest = { confirmarCancelacion = false },
            title = { Text("¿Cancelar cita?") },
            text = { Text("Esta acción cambiará el estado de tu cita a Cancelada.") },
            confirmButton = {
                Button(onClick = {
                    confirmarCancelacion = false
                    viewModel.cancelar()
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelacion = false }) { Text("Volver") }
            },
        )
    }
}

@Composable
private fun DatoDetalle(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
