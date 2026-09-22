package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede

/** Contrato del catálogo y de las citas, independiente del origen de los datos. */
interface CitaRepository {
    suspend fun pacienteActual(): Paciente
    suspend fun sedes(): List<Sede>
    suspend fun especialidades(): List<Especialidad>
    suspend fun medicos(): List<Medico>
    suspend fun listarCitas(): List<Cita>
    suspend fun obtenerCita(id: Long): Cita?
    suspend fun registrarCita(cita: Cita): Cita
    suspend fun actualizarCita(cita: Cita): Cita
}
