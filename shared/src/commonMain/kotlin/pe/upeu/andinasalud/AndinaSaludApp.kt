package pe.upeu.andinasalud

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioScreen
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.navigation.AppBackHandler
import pe.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudScreen
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndinaSaludApp() {
    var ruta by rememberSaveable { mutableStateOf("inicio") }
    var retorno by rememberSaveable { mutableStateOf("inicio") }
    var citaId by rememberSaveable { mutableLongStateOf(-1L) }
    var modoOscuro by rememberSaveable { mutableStateOf(false) }

    val inicioViewModel: InicioViewModel = koinViewModel()
    val citasViewModel: CitasViewModel = koinViewModel()
    val detalleViewModel: DetalleCitaViewModel = koinViewModel()
    val solicitudViewModel: SolicitudViewModel = koinViewModel()
    val perfilViewModel: PerfilViewModel = koinViewModel()

    fun refrescar() {
        inicioViewModel.cargar()
        citasViewModel.cargar()
    }
    fun irA(destino: String) {
        ruta = destino
        if (destino == "inicio" || destino == "citas") refrescar()
    }
    fun abrirDetalle(id: Long) {
        retorno = ruta
        citaId = id
        ruta = "detalle"
    }
    fun abrirSolicitud() {
        retorno = ruta
        ruta = "solicitud"
    }
    fun volver() {
        ruta = if (ruta == "detalle" || ruta == "solicitud") retorno else "inicio"
        refrescar()
    }

    AppBackHandler(enabled = ruta != "inicio", onBack = ::volver)
    AndinaSaludTheme(oscuro = modoOscuro) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(when (ruta) {
                                "inicio" -> "AndinaSalud"
                                "citas" -> "Mis citas"
                                "perfil" -> "Mi perfil"
                                "detalle" -> "Detalle de cita"
                                else -> "Solicitar cita"
                            })
                        },
                        navigationIcon = {
                            if (ruta == "detalle" || ruta == "solicitud") {
                                IconButton(onClick = ::volver) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        },
                    )
                },
                bottomBar = {
                    if (ruta != "detalle" && ruta != "solicitud") {
                        NavigationBar {
                            NavigationBarItem(
                                selected = ruta == "inicio",
                                onClick = { irA("inicio") },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") },
                            )
                            NavigationBarItem(
                                selected = ruta == "citas",
                                onClick = { irA("citas") },
                                icon = { Icon(Icons.Default.Event, contentDescription = "Citas") },
                                label = { Text("Citas") },
                            )
                            NavigationBarItem(
                                selected = ruta == "perfil",
                                onClick = { irA("perfil") },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                                label = { Text("Perfil") },
                            )
                        }
                    }
                },
            ) { padding ->
                val contenido = Modifier.fillMaxSize().padding(padding)
                when (ruta) {
                    "inicio" -> InicioScreen(inicioViewModel, onMisCitas = { irA("citas") },
                        onSolicitar = ::abrirSolicitud, onDetalle = ::abrirDetalle,
                        modifier = contenido)
                    "citas" -> CitasScreen(citasViewModel, onDetalle = ::abrirDetalle,
                        onSolicitar = ::abrirSolicitud, modifier = contenido)
                    "perfil" -> PerfilScreen(perfilViewModel, modoOscuro = modoOscuro,
                        onModoOscuroChange = { modoOscuro = it }, modifier = contenido)
                    "detalle" -> DetalleCitaScreen(citaId, detalleViewModel, modifier = contenido)
                    "solicitud" -> SolicitudScreen(solicitudViewModel, modifier = contenido)
                }
            }
        }
    }
}
