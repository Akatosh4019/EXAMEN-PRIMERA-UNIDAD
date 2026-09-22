package pe.upeu.andinasalud.data.local

import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import kotlin.time.Duration.Companion.days

/** Las citas programadas se calculan desde hoy para que siempre sigan siendo futuras. */
object CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Roberto Samuel Valencia Saavedra",
        documento = "72870236",
        correo = "samuelvalencia3019@gmail.com",
        telefono = "917 595 344",
    )

    val sedes = listOf(
        Sede("nana", "Ñaña"),
        Sede("chosica", "Chosica"),
        Sede("chaclacayo", "Chaclacayo"),
        Sede("santa-anita", "Santa Anita"),
    )

    val especialidades = listOf(
        Especialidad("medicina-general", "Medicina General"),
        Especialidad("odontologia", "Odontología"),
        Especialidad("pediatria", "Pediatría"),
        Especialidad("nutricion", "Nutrición"),
        Especialidad("psicologia", "Psicología"),
    )

    val medicos = listOf(
        Medico("M01", "Dr. Iván Rojas", "medicina-general", setOf("nana", "santa-anita")),
        Medico("M02", "Dra. Elena Vega", "medicina-general", setOf("chosica", "chaclacayo")),
        Medico("M03", "Dra. Rosa Flores", "odontologia", setOf("chosica", "santa-anita")),
        Medico("M04", "Dr. Carlos Paredes", "odontologia", setOf("nana", "chaclacayo")),
        Medico("M05", "Dra. Carla Núñez", "pediatria", setOf("chaclacayo", "santa-anita")),
        Medico("M06", "Dr. Pablo Arias", "pediatria", setOf("nana", "chosica")),
        Medico("M07", "Lic. Ana Bermúdez", "nutricion", setOf("santa-anita", "chaclacayo")),
        Medico("M08", "Lic. Mariela Torres", "nutricion", setOf("nana", "chosica")),
        Medico("M09", "Ps. Luis Tapia", "psicologia", setOf("nana", "santa-anita")),
        Medico("M10", "Ps. Teresa Salas", "psicologia", setOf("chosica", "chaclacayo")),
    )

    fun citas(reloj: RelojClinico): List<Cita> {
        val ahora = reloj.ahora()
        fun fechaEn(dias: Int) = (ahora + dias.days).toLocalDateTime(reloj.zona()).date
        val pacienteId = paciente.id
        return listOf(
            Cita(1, pacienteId, "medicina-general", "M01", "nana", fechaEn(2), LocalTime(9, 0),
                "Consulta médica general", EstadoCita.Programada(true)),
            Cita(2, pacienteId, "odontologia", "M03", "chosica", fechaEn(5), LocalTime(16, 30),
                "Evaluación odontológica", EstadoCita.Programada(false), ModalidadAtencion.Teleconsulta),
            Cita(3, pacienteId, "nutricion", "M07", "santa-anita", fechaEn(9), LocalTime(11, 15),
                "Seguimiento nutricional", EstadoCita.Programada(true)),
            Cita(4, pacienteId, "pediatria", "M05", "chaclacayo", fechaEn(-4), LocalTime(8, 45),
                "Control pediátrico", EstadoCita.Atendida("Control en tres meses")),
            Cita(5, pacienteId, "psicologia", "M09", "nana", fechaEn(-8), LocalTime(15, 0),
                "Consulta de seguimiento", EstadoCita.Atendida("Continuar sesiones quincenales")),
            Cita(6, pacienteId, "medicina-general", "M02", "chosica", fechaEn(-2), LocalTime(10, 30),
                "Consulta de rutina", EstadoCita.Cancelada("Viaje del paciente", true)),
        )
    }
}
