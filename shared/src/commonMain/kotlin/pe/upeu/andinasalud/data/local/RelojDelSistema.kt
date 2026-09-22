package pe.upeu.andinasalud.data.local

import kotlinx.datetime.TimeZone
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import kotlin.time.Clock
import kotlin.time.Instant

class RelojDelSistema : RelojClinico {
    override fun ahora(): Instant = Clock.System.now()
    override fun zona(): TimeZone = TimeZone.currentSystemDefault()
}
