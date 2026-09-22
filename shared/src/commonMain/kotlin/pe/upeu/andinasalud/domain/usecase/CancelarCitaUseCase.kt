package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import kotlin.time.Duration.Companion.hours

class CancelarCitaUseCase(
    private val repositorio: CitaRepository,
    private val reloj: RelojClinico,
) {
    suspend operator fun invoke(id: Long, motivo: String): Cita {
        val cita = repositorio.obtenerCita(id)
            ?: throw ReglaCitaException("La cita no existe")
        if (cita.estado !is EstadoCita.Programada) {
            throw ReglaCitaException("Solo se puede cancelar una cita programada")
        }
        val faltante = cita.fecha.atTime(cita.hora).toInstant(reloj.zona()) - reloj.ahora()
        if (faltante <= 24.hours) {
            throw ReglaCitaException("La cita solo puede cancelarse con más de 24 horas de anticipación")
        }
        return repositorio.actualizarCita(
            cita.copy(estado = EstadoCita.Cancelada(motivo.trim(), true))
        )
    }
}
