package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ReprogramarCitaUseCase(
    private val repositorio: CitaRepository,
    private val validarHorario: ValidarHorarioCita,
) {
    suspend operator fun invoke(id: Long, fechaTexto: String, horaTexto: String) = run {
        val cita = repositorio.obtenerCita(id) ?: throw ReglaCitaException("La cita no existe")
        if (cita.estado !is EstadoCita.Programada) {
            throw ReglaCitaException("Solo se puede reprogramar una cita programada")
        }
        val errores = mutableMapOf<String, String>()
        val (fecha, hora) = validarHorario.fechaHora(fechaTexto, horaTexto, errores)
        if (errores.isNotEmpty()) throw CitaInvalidaException(errores)
        val fechaValida = requireNotNull(fecha)
        val horaValida = requireNotNull(hora)
        val programadas = repositorio.listarCitas().filter {
            it.pacienteId == cita.pacienteId && it.estado is EstadoCita.Programada
        }
        if (validarHorario.horarioOcupado(programadas, fechaValida, horaValida, exceptoId = id)) {
            throw CitaInvalidaException(mapOf("hora" to "Ya tienes una cita programada en ese horario"))
        }
        repositorio.actualizarCita(cita.copy(fecha = fechaValida, hora = horaValida))
    }
}
