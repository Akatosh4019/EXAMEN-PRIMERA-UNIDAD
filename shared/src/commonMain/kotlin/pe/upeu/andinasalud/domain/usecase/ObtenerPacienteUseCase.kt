package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerPacienteUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke() = repositorio.pacienteActual()
}
