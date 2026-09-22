package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.RelojClinico

class CitaRepositoryFake(reloj: RelojClinico) : CitaRepository {
    private val acceso = Mutex()
    private val citas = CitasSimuladas.citas(reloj).toMutableList()
    private var siguienteId = (citas.maxOfOrNull { it.id } ?: 0L) + 1L

    /** Útil para comprobar la vista de error con una carga simulada. */
    var fallarSiguienteCarga: Boolean = false

    override suspend fun pacienteActual(): Paciente = CitasSimuladas.paciente
    override suspend fun sedes(): List<Sede> = CitasSimuladas.sedes
    override suspend fun especialidades(): List<Especialidad> = CitasSimuladas.especialidades
    override suspend fun medicos(): List<Medico> = CitasSimuladas.medicos

    override suspend fun listarCitas(): List<Cita> {
        delay(800)
        if (fallarSiguienteCarga) {
            fallarSiguienteCarga = false
            throw IllegalStateException("No se pudieron cargar las citas")
        }
        return acceso.withLock { citas.toList() }
    }

    override suspend fun obtenerCita(id: Long): Cita? {
        delay(800)
        return acceso.withLock { citas.firstOrNull { it.id == id } }
    }

    override suspend fun registrarCita(cita: Cita): Cita = acceso.withLock {
        cita.copy(id = siguienteId++).also(citas::add)
    }

    override suspend fun actualizarCita(cita: Cita): Cita = acceso.withLock {
        val indice = citas.indexOfFirst { it.id == cita.id }
        require(indice >= 0) { "La cita no existe" }
        citas[indice] = cita
        cita
    }
}
