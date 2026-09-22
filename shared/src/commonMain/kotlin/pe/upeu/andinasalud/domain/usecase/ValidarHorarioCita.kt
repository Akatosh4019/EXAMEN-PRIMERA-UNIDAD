package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita

/** Validaciones compartidas por el alta y la reprogramación de citas. */
class ValidarHorarioCita(private val reloj: RelojClinico) {
    fun fechaHora(
        fechaTexto: String,
        horaTexto: String,
        errores: MutableMap<String, String>,
    ): Pair<LocalDate?, LocalTime?> {
        val fecha = try {
            LocalDate.parse(fechaTexto.trim())
        } catch (_: IllegalArgumentException) {
            errores["fecha"] = "Ingresa una fecha válida (AAAA-MM-DD)"
            null
        }
        val hora = try {
            LocalTime.parse(horaTexto.trim())
        } catch (_: IllegalArgumentException) {
            errores["hora"] = "Ingresa una hora válida (HH:MM)"
            null
        }
        if (fecha != null && hora != null &&
            fecha.atTime(hora).toInstant(reloj.zona()) <= reloj.ahora()
        ) {
            errores["fecha"] = "La cita debe ser en una fecha y hora futuras"
        }
        return fecha to hora
    }

    fun horarioOcupado(
        programadas: List<Cita>,
        fecha: LocalDate,
        hora: LocalTime,
        exceptoId: Long? = null,
    ): Boolean = programadas.any {
        it.id != exceptoId && it.fecha == fecha && it.hora == hora
    }
}
