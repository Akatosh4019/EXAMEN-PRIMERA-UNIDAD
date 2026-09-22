package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CatalogoClinico
import pe.upeu.andinasalud.domain.usecase.CupoCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import pe.upeu.andinasalud.presentation.common.FiltroCitas
import pe.upeu.andinasalud.presentation.common.aUi
import pe.upeu.andinasalud.presentation.common.sinTildes

class CitasViewModel(
    private val obtenerCitas: ObtenerCitasUseCase,
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val reloj: RelojClinico,
    private val cupoCitas: CupoCitasUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState = _uiState.asStateFlow()
    private var citas = emptyList<Cita>()
    private var catalogo: CatalogoClinico? = null

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(fase = FaseCitas.Cargando) }
            try {
                catalogo = obtenerCatalogo()
                citas = obtenerCitas()
                val cantidad = cupoCitas.cantidadProgramadas(citas)
                _uiState.update {
                    it.copy(programadas = cantidad, puedeSolicitar = cupoCitas.puedeSolicitar(cantidad))
                }
                filtrar()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(fase = FaseCitas.Error("No se pudieron cargar las citas")) }
            }
        }
    }

    fun cambiarFiltro(filtro: FiltroCitas) {
        _uiState.update { it.copy(filtro = filtro) }
        filtrar()
    }

    fun cambiarBusqueda(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
        filtrar()
    }

    fun cambiarSoloHoy() {
        _uiState.update { it.copy(soloHoy = !it.soloHoy) }
        filtrar()
    }

    private fun filtrar() {
        val datos = catalogo ?: return
        val estado = _uiState.value
        val termino = estado.busqueda.trim().sinTildes()
        val hoy = reloj.ahora().toLocalDateTime(reloj.zona()).date
        val visibles = citas.map { it.aUi(datos) }.filter { cita ->
            (estado.filtro == FiltroCitas.Todas || cita.filtro == estado.filtro) &&
                (!estado.soloHoy || cita.fecha == hoy.toString()) &&
                (termino.isEmpty() || cita.especialidad.sinTildes().contains(termino) ||
                    cita.medico.sinTildes().contains(termino))
        }
        _uiState.update {
            it.copy(fase = if (visibles.isEmpty()) FaseCitas.Vacia else FaseCitas.Contenido(visibles))
        }
    }
}
