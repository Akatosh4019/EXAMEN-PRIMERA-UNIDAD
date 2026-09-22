package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.CitaTarjeta
import pe.upeu.andinasalud.presentation.common.EstadoPanel
import pe.upeu.andinasalud.presentation.common.FiltroCitas

@Composable
fun CitasScreen(
    viewModel: CitasViewModel,
    onDetalle: (Long) -> Unit,
    onSolicitar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estado by viewModel.uiState.collectAsState()
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = viewModel::cambiarBusqueda,
            label = { Text("Buscar especialidad o médico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = estado.soloHoy,
                onClick = viewModel::cambiarSoloHoy,
                label = { Text("Hoy") },
            )
            FiltroCitas.entries.forEach { filtro ->
                FilterChip(
                    selected = estado.filtro == filtro,
                    onClick = { viewModel.cambiarFiltro(filtro) },
                    label = { Text(filtro.etiqueta) },
                )
            }
        }
        Button(onClick = onSolicitar, modifier = Modifier.fillMaxWidth()) { Text("Solicitar cita") }
        Text("Mis citas", style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        when (val fase = estado.fase) {
            FaseCitas.Cargando -> EstadoPanel("Cargando citas…", "Espera un momento", cargando = true)
            FaseCitas.Vacia -> EstadoPanel("Sin citas", "No hay citas para el filtro o búsqueda actual")
            is FaseCitas.Error -> EstadoPanel("Error de carga", fase.mensaje,
                error = true, reintentar = viewModel::cargar)
            is FaseCitas.Contenido -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(fase.citas, key = { it.id }) { cita ->
                    CitaTarjeta(cita, onClick = { onDetalle(cita.id) })
                }
            }
        }
    }
}
