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
    fun puedeCancelar(cita: Cita): Boolean =
        cita.estado is EstadoCita.Programada &&
            cita.fecha.atTime(cita.hora).toInstant(reloj.zona()) - reloj.ahora() > 24.hours

    suspend operator fun invoke(id: Long, motivo: String): Cita {
        val cita = repositorio.obtenerCita(id)
            ?: throw ReglaCitaException("La cita no existe")
        if (cita.estado !is EstadoCita.Programada) {
            throw ReglaCitaException("Solo se puede cancelar una cita programada")
        }
        if (!puedeCancelar(cita)) {
            throw ReglaCitaException("La cita solo puede cancelarse con más de 24 horas de anticipación")
        }
        return repositorio.actualizarCita(
            cita.copy(estado = EstadoCita.Cancelada(motivo.trim(), true))
        )
    }
}
