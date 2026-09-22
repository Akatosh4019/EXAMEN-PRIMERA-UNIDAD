package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class SolicitarCitaUseCase(
    private val repositorio: CitaRepository,
    private val reloj: RelojClinico,
) {
    suspend operator fun invoke(
        especialidadId: String,
        sedeId: String,
        fechaTexto: String,
        horaTexto: String,
        motivoTexto: String,
    ): Cita {
        val errores = mutableMapOf<String, String>()
        val especialidad = repositorio.especialidades().firstOrNull { it.id == especialidadId }
        val sede = repositorio.sedes().firstOrNull { it.id == sedeId }
        if (especialidad == null) errores["especialidad"] = "Selecciona una especialidad"
        if (sede == null) errores["sede"] = "Selecciona una sede"

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
        val motivo = motivoTexto.trim()
        if (motivo.length !in 10..200) {
            errores["motivo"] = "El motivo debe tener entre 10 y 200 caracteres"
        }
        if (fecha != null && hora != null &&
            fecha.atTime(hora).toInstant(reloj.zona()) <= reloj.ahora()
        ) {
            errores["fecha"] = "La cita debe ser en una fecha y hora futuras"
        }
        if (errores.isNotEmpty()) throw CitaInvalidaException(errores)

        val fechaValida = requireNotNull(fecha)
        val horaValida = requireNotNull(hora)
        val medico = repositorio.medicos().firstOrNull {
            it.especialidadId == especialidadId && sedeId in it.sedeIds
        } ?: throw CitaInvalidaException(
            mapOf("sede" to "No hay médicos disponibles para esa especialidad y sede")
        )
        val paciente = repositorio.pacienteActual()
        val programadas = repositorio.listarCitas().filter {
            it.pacienteId == paciente.id && it.estado is EstadoCita.Programada
        }
        if (programadas.size >= 3) {
            throw CitaInvalidaException(mapOf("general" to "Ya tienes tres citas programadas"))
        }
        if (programadas.any { it.fecha == fechaValida && it.hora == horaValida }) {
            throw CitaInvalidaException(mapOf("hora" to "Ya tienes una cita programada en ese horario"))
        }
        return repositorio.registrarCita(
            Cita(
                id = 0L,
                pacienteId = paciente.id,
                especialidadId = especialidadId,
                medicoId = medico.id,
                sedeId = sedeId,
                fecha = fechaValida,
                hora = horaValida,
                motivo = motivo,
                estado = EstadoCita.Programada(recordatorioActivo = true),
            )
        )
    }
}
