package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Cita(
    val id: Long,
    val pacienteId: String,
    val especialidadId: String,
    val medicoId: String,
    val sedeId: String,
    val fecha: LocalDate,
    val hora: LocalTime,
    val motivo: String,
    val estado: EstadoCita,
    val modalidad: ModalidadAtencion = ModalidadAtencion.Presencial,
)
