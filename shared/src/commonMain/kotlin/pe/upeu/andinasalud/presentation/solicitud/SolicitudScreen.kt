package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.EstadoPanel

@Composable
fun SolicitudScreen(viewModel: SolicitudViewModel, modifier: Modifier = Modifier) {
    val estado by viewModel.uiState.collectAsState()
    when (val fase = estado.fase) {
        FaseSolicitud.Cargando -> EstadoPanel("Cargando formulario…", "Consultando sedes y especialidades",
            cargando = true, modifier = modifier)
        is FaseSolicitud.Error -> EstadoPanel("Error de carga", fase.mensaje,
            error = true, reintentar = viewModel::cargar, modifier = modifier)
        is FaseSolicitud.Lista -> {
            Column(
                modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Solicitar cita", style = MaterialTheme.typography.headlineSmall)
                Text("Completa los datos para encontrar una atención disponible.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SelectorCita(
                            titulo = "Especialidad",
                            valor = fase.catalogo.especialidades.firstOrNull {
                                it.id == estado.formulario.especialidadId
                            }?.nombre ?: "Seleccionar especialidad",
                            opciones = fase.catalogo.especialidades.map { it.id to it.nombre },
                            error = estado.errores["especialidad"],
                            onSeleccionar = viewModel::cambiarEspecialidad,
                        )
                        SelectorCita(
                            titulo = "Sede",
                            valor = fase.catalogo.sedes.firstOrNull {
                                it.id == estado.formulario.sedeId
                            }?.nombre ?: "Seleccionar sede",
                            opciones = fase.catalogo.sedes.map { it.id to it.nombre },
                            error = estado.errores["sede"],
                            onSeleccionar = viewModel::cambiarSede,
                        )
                        CampoSolicitud(
                            valor = estado.formulario.fecha,
                            onCambio = viewModel::cambiarFecha,
                            titulo = "Fecha (AAAA-MM-DD)",
                            error = estado.errores["fecha"],
                        )
                        CampoSolicitud(
                            valor = estado.formulario.hora,
                            onCambio = viewModel::cambiarHora,
                            titulo = "Hora (HH:MM)",
                            error = estado.errores["hora"],
                        )
                        CampoSolicitud(
                            valor = estado.formulario.motivo,
                            onCambio = viewModel::cambiarMotivo,
                            titulo = "Motivo de consulta",
                            error = estado.errores["motivo"],
                            minLines = 3,
                        )
                        estado.errores["general"]?.let {
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = viewModel::solicitar,
                            enabled = !estado.enviando,
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(if (estado.enviando) "Solicitando…" else "Solicitar cita") }
                    }
                }
                estado.mensajeExito?.let {
                    Card(Modifier.fillMaxWidth()) {
                        Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorCita(
    titulo: String,
    valor: String,
    opciones: List<Pair<String, String>>,
    error: String?,
    onSeleccionar: (String) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) {
                Text(valor)
            }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { (id, nombre) ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            onSeleccionar(id)
                            expandido = false
                        },
                    )
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun CampoSolicitud(
    valor: String,
    onCambio: (String) -> Unit,
    titulo: String,
    error: String?,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(titulo) },
        isError = error != null,
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
    )
}
