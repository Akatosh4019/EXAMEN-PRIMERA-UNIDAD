package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.common.EstadoPanel

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fase by viewModel.fase.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineSmall)
        when (val actual = fase) {
            FasePerfil.Cargando -> EstadoPanel("Cargando perfil…", "Consultando tus datos", cargando = true)
            is FasePerfil.Error -> EstadoPanel("Error de carga", actual.mensaje,
                error = true, reintentar = viewModel::cargar)
            is FasePerfil.Contenido -> Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DatoPerfil("Nombre", actual.paciente.nombre)
                    DatoPerfil("Documento", actual.paciente.documento)
                    DatoPerfil("Correo", actual.paciente.correo)
                    DatoPerfil("Teléfono", actual.paciente.telefono)
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Modo oscuro", style = MaterialTheme.typography.titleMedium)
                    Text("Cambiar el aspecto de la aplicación",
                        style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = modoOscuro, onCheckedChange = onModoOscuroChange)
            }
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
