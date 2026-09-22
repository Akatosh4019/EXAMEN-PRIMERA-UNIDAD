package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.usecase.CatalogoClinico
import pe.upeu.andinasalud.domain.usecase.CitaInvalidaException
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

sealed interface FaseSolicitud {
    data object Cargando : FaseSolicitud
    data class Lista(val catalogo: CatalogoClinico) : FaseSolicitud
    data class Error(val mensaje: String) : FaseSolicitud
}

data class FormularioSolicitud(
    val especialidadId: String = "",
    val sedeId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    val modalidad: ModalidadAtencion = ModalidadAtencion.Presencial,
)

data class SolicitudUiState(
    val fase: FaseSolicitud = FaseSolicitud.Cargando,
    val formulario: FormularioSolicitud = FormularioSolicitud(),
    val errores: Map<String, String> = emptyMap(),
    val enviando: Boolean = false,
    val mensajeExito: String? = null,
)

class SolicitudViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val solicitarCita: SolicitarCitaUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(fase = FaseSolicitud.Cargando) }
            try {
                delay(800)
                _uiState.update { it.copy(fase = FaseSolicitud.Lista(obtenerCatalogo())) }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(fase = FaseSolicitud.Error("No se pudo cargar el formulario")) }
            }
        }
    }

    fun cambiarEspecialidad(valor: String) = cambiar("especialidad") { copy(especialidadId = valor) }
    fun cambiarSede(valor: String) = cambiar("sede") { copy(sedeId = valor) }
    fun cambiarFecha(valor: String) = cambiar("fecha") { copy(fecha = valor) }
    fun cambiarHora(valor: String) = cambiar("hora") { copy(hora = valor) }
    fun cambiarMotivo(valor: String) = cambiar("motivo") { copy(motivo = valor) }
    fun cambiarModalidad(valor: ModalidadAtencion) = cambiar("modalidad") { copy(modalidad = valor) }

    private fun cambiar(campo: String, cambio: FormularioSolicitud.() -> FormularioSolicitud) {
        _uiState.update {
            it.copy(formulario = it.formulario.cambio(), errores = it.errores - campo - "general",
                mensajeExito = null)
        }
    }

    fun solicitar() {
        val actual = _uiState.value
        if (actual.enviando || actual.fase !is FaseSolicitud.Lista) return
        _uiState.update { it.copy(enviando = true, errores = emptyMap(), mensajeExito = null) }
        viewModelScope.launch {
            try {
                val f = actual.formulario
                val cita = solicitarCita(f.especialidadId, f.sedeId, f.fecha, f.hora, f.motivo,
                    f.modalidad)
                _uiState.update {
                    it.copy(formulario = FormularioSolicitud(), enviando = false,
                        mensajeExito = "Cita #${cita.id} solicitada correctamente")
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: CitaInvalidaException) {
                _uiState.update { it.copy(errores = error.errores, enviando = false) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errores = mapOf("general" to (error.message ?: "No se pudo solicitar la cita")),
                        enviando = false)
                }
            }
        }
    }
}
