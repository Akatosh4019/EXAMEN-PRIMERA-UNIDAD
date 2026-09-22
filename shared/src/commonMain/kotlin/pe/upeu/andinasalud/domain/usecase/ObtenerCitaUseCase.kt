package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitaUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(id: Long) = repositorio.obtenerCita(id)
}
