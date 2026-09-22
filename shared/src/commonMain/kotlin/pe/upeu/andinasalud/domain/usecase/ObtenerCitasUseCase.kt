package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(): List<Cita> = repositorio.listarCitas()
        .sortedWith(compareBy<Cita> { it.fecha }.thenBy { it.hora })
}
