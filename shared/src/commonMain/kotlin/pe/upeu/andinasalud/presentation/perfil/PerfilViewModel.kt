package pe.upeu.andinasalud.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase

sealed interface FasePerfil {
    data object Cargando : FasePerfil
    data class Contenido(val paciente: Paciente) : FasePerfil
    data class Error(val mensaje: String) : FasePerfil
}

class PerfilViewModel(private val obtenerPaciente: ObtenerPacienteUseCase) : ViewModel() {
    private val _fase = MutableStateFlow<FasePerfil>(FasePerfil.Cargando)
    val fase = _fase.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _fase.value = FasePerfil.Cargando
            try {
                delay(800)
                _fase.value = FasePerfil.Contenido(obtenerPaciente())
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _fase.value = FasePerfil.Error("No se pudo cargar el perfil")
            }
        }
    }
}
