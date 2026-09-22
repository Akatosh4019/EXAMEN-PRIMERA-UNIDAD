package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.CitaTarjeta
import pe.upeu.andinasalud.presentation.common.EstadoPanel

@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onMisCitas: () -> Unit,
    onSolicitar: () -> Unit,
    onDetalle: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fase by viewModel.fase.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        when (val actual = fase) {
            FaseInicio.Cargando -> EstadoPanel("Cargando inicio…", "Buscando tus próximas citas", cargando = true)
            is FaseInicio.Error -> EstadoPanel("No se pudo cargar", actual.mensaje,
                error = true, reintentar = viewModel::cargar)
            is FaseInicio.Contenido -> {
                Text("Hola, ${actual.nombre.substringBefore(' ')}", style = MaterialTheme.typography.headlineMedium)
                Text("Tu salud, cerca de ti", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Próxima cita", style = MaterialTheme.typography.titleLarge)
                val cita = actual.proximaCita
                if (cita == null) {
                    Card(Modifier.fillMaxWidth()) {
                        Text("No tienes una próxima cita programada", Modifier.padding(20.dp))
                    }
                } else {
                    CitaTarjeta(cita, onClick = { onDetalle(cita.id) })
                }
            }
        }
        Text("Accesos rápidos", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onMisCitas, modifier = Modifier.fillMaxWidth()) { Text("Mis citas") }
        OutlinedButton(onClick = onSolicitar, modifier = Modifier.fillMaxWidth()) {
            Text("Solicitar cita")
        }
    }
}
