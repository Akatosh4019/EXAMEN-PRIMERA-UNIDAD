package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.CitaInvalidaException
import pe.upeu.andinasalud.presentation.common.CitaUi
import pe.upeu.andinasalud.presentation.common.aUi

sealed interface FaseDetalle {
    data object Cargando : FaseDetalle
    data object Vacia : FaseDetalle
    data class Contenido(
        val cita: CitaUi,
        val puedeCancelar: Boolean,
        val cancelando: Boolean = false,
        val errorAccion: String? = null,
        val reprogramando: Boolean = false,
        val erroresHorario: Map<String, String> = emptyMap(),
        val mensajeExito: String? = null,
    ) : FaseDetalle
    data class Error(val mensaje: String) : FaseDetalle
}

class DetalleCitaViewModel(
    private val obtenerCita: ObtenerCitaUseCase,
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val cancelarCita: CancelarCitaUseCase,
    private val reprogramarCita: ReprogramarCitaUseCase,
) : ViewModel() {
    private val _fase = MutableStateFlow<FaseDetalle>(FaseDetalle.Cargando)
    val fase = _fase.asStateFlow()
    private var citaId: Long = -1L

    fun abrir(id: Long) {
        citaId = id
        cargar()
    }

    fun cargar() {
        if (citaId < 0) return
        viewModelScope.launch {
            _fase.value = FaseDetalle.Cargando
            try {
                val catalogo = obtenerCatalogo()
                val cita = obtenerCita(citaId)
                _fase.value = if (cita == null) FaseDetalle.Vacia else FaseDetalle.Contenido(
                    cita = cita.aUi(catalogo),
                    puedeCancelar = cancelarCita.puedeCancelar(cita),
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _fase.value = FaseDetalle.Error("No se pudo cargar el detalle")
            }
        }
    }

    fun cancelar() {
        val actual = _fase.value as? FaseDetalle.Contenido ?: return
        if (!actual.puedeCancelar || actual.cancelando) return
        viewModelScope.launch {
            _fase.value = actual.copy(cancelando = true, errorAccion = null)
            try {
                val cancelada = cancelarCita(citaId, "Cancelada por el paciente")
                val catalogo = obtenerCatalogo()
                _fase.value = FaseDetalle.Contenido(cancelada.aUi(catalogo), puedeCancelar = false)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _fase.value = actual.copy(errorAccion = error.message ?: "No se pudo cancelar")
            }
        }
    }

    fun reprogramar(fecha: String, hora: String) {
        val actual = _fase.value as? FaseDetalle.Contenido ?: return
        if (actual.cita.estado != "Programada" || actual.reprogramando) return
        viewModelScope.launch {
            _fase.value = actual.copy(reprogramando = true, erroresHorario = emptyMap(),
                errorAccion = null, mensajeExito = null)
            try {
                val cambiada = reprogramarCita(citaId, fecha, hora)
                _fase.value = actual.copy(cita = cambiada.aUi(obtenerCatalogo()),
                    puedeCancelar = cancelarCita.puedeCancelar(cambiada),
                    reprogramando = false, mensajeExito = "Cita reprogramada correctamente")
            } catch (error: CancellationException) {
                throw error
            } catch (error: CitaInvalidaException) {
                _fase.value = actual.copy(erroresHorario = error.errores, reprogramando = false)
            } catch (error: Exception) {
                _fase.value = actual.copy(errorAccion = error.message ?: "No se pudo reprogramar",
                    reprogramando = false)
            }
        }
    }
}
