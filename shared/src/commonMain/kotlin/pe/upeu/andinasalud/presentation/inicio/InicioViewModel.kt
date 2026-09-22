package pe.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.presentation.common.CitaUi
import pe.upeu.andinasalud.presentation.common.aUi

sealed interface FaseInicio {
    data object Cargando : FaseInicio
    data class Contenido(val nombre: String, val proximaCita: CitaUi?) : FaseInicio
    data class Error(val mensaje: String) : FaseInicio
}

class InicioViewModel(
    private val obtenerPaciente: ObtenerPacienteUseCase,
    private val obtenerCitas: ObtenerCitasUseCase,
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
) : ViewModel() {
    private val _fase = MutableStateFlow<FaseInicio>(FaseInicio.Cargando)
    val fase = _fase.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _fase.value = FaseInicio.Cargando
            try {
                val paciente = obtenerPaciente()
                val catalogo = obtenerCatalogo()
                val proxima = obtenerCitas().firstOrNull { it.estado is EstadoCita.Programada }
                _fase.value = FaseInicio.Contenido(paciente.nombre, proxima?.aUi(catalogo))
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _fase.value = FaseInicio.Error("No se pudo cargar el inicio")
            }
        }
    }
}
