package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(
    private val repositorio: CitaRepository,
    private val reloj: RelojClinico,
) {
    suspend operator fun invoke(): List<Cita> {
        val ahora = reloj.ahora()
        val zona = reloj.zona()
        // Las próximas van primero; el historial se presenta del más reciente al más antiguo.
        return repositorio.listarCitas().sortedWith(
            compareBy<Cita> { it.fecha.atTime(it.hora).toInstant(zona) < ahora }
                .thenBy {
                    val instante = it.fecha.atTime(it.hora).toInstant(zona)
                    if (instante < ahora) -instante.epochSeconds else instante.epochSeconds
                }
        )
    }
}
