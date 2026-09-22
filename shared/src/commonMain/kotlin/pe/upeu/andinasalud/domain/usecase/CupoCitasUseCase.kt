package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

/** Fuente única para RN-02, tanto al registrar como al mostrar el cupo. */
class CupoCitasUseCase(private val repositorio: CitaRepository) {
    companion object { const val LIMITE = 3 }

    suspend fun cantidadProgramadas(citas: List<Cita>): Int {
        val pacienteId = repositorio.pacienteActual().id
        return citas.count { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada }
    }

    fun puedeSolicitar(cantidadProgramadas: Int): Boolean = cantidadProgramadas < LIMITE
}
