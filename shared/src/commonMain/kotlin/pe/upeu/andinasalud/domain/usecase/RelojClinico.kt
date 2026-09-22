package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** Permite evaluar reglas de fecha con un reloj controlable en las pruebas. */
interface RelojClinico {
    fun ahora(): Instant
    fun zona(): TimeZone
}
