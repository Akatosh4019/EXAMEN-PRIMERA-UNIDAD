package pe.upeu.andinasalud.domain

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.CupoCitasUseCase
import pe.upeu.andinasalud.domain.usecase.CitaInvalidaException
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ReglaCitaException
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ValidarHorarioCita
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

private class RelojFijo : RelojClinico {
    override fun ahora(): Instant = Instant.parse("2026-09-22T12:00:00Z")
    override fun zona(): TimeZone = TimeZone.UTC
}

class ReglasCitaTest {
    private val reloj = RelojFijo()

    @Test
    fun `datos semilla contienen tres citas futuras y diez medicos`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        assertEquals(10, repo.medicos().size)
        assertEquals(3, repo.listarCitas().count { it.estado is EstadoCita.Programada })
    }

    @Test
    fun `RN01 impide solicitar una cita pasada`() = runTest {
        val error = errorDe {
            solicitar(CitaRepositoryFake(reloj))(
                "medicina-general", "nana", "2026-09-21", "09:00", "Consulta médica general"
            )
        }
        assertTrue("fecha" in error.errores)
    }

    @Test
    fun `RN02 impide una cuarta cita programada`() = runTest {
        val error = errorDe {
            solicitar(CitaRepositoryFake(reloj))(
                "medicina-general", "nana", "2026-10-12", "09:00", "Consulta médica general"
            )
        }
        assertTrue("general" in error.errores)
    }

    @Test
    fun `SC-B contador y disponibilidad usan la misma regla RN02`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val cupo = CupoCitasUseCase(repo)
        assertEquals(3, cupo.cantidadProgramadas(repo.listarCitas()))
        assertTrue(!cupo.puedeSolicitar(3))
        CancelarCitaUseCase(repo, reloj)(1, "Liberar cupo")
        assertTrue(cupo.puedeSolicitar(cupo.cantidadProgramadas(repo.listarCitas())))
    }

    @Test
    fun `RN03 permite cancelar solo una cita programada con mas de 24 horas`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val cancelar = CancelarCitaUseCase(repo, reloj)
        assertTrue(cancelar.puedeCancelar(requireNotNull(repo.obtenerCita(1))))
        assertTrue(cancelar(1, "Cambio de planes").estado is EstadoCita.Cancelada)
        val atendida = requireNotNull(repo.obtenerCita(4))
        assertTrue(!cancelar.puedeCancelar(atendida))
        try {
            cancelar(4, "No procede")
            throw AssertionError("Debió rechazar una cita atendida")
        } catch (_: ReglaCitaException) { }
    }

    @Test
    fun `RN04 exige un motivo entre diez y doscientos caracteres`() = runTest {
        val error = errorDe {
            solicitar(CitaRepositoryFake(reloj))(
                "medicina-general", "nana", "2026-10-12", "09:00", "Corto"
            )
        }
        assertTrue("motivo" in error.errores)
    }

    @Test
    fun `RN05 impide duplicar el horario de una cita programada`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        CancelarCitaUseCase(repo, reloj)(3, "Liberar un cupo")
        val ocupada = requireNotNull(repo.obtenerCita(2))
        val error = errorDe {
            solicitar(repo)(
                "medicina-general", "nana", ocupada.fecha.toString(), ocupada.hora.toString(),
                "Consulta médica general"
            )
        }
        assertTrue("hora" in error.errores)
    }

    @Test
    fun `una solicitud valida se registra despues de liberar un cupo`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        CancelarCitaUseCase(repo, reloj)(3, "Liberar un cupo")
        val creada = solicitar(repo)(
            "medicina-general", "nana", "2026-10-12", "09:00", "Consulta médica general"
        )
        assertEquals(7L, creada.id)
        assertTrue(creada.estado is EstadoCita.Programada)
    }

    @Test
    fun `SC-C conserva la modalidad elegida al registrar`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        CancelarCitaUseCase(repo, reloj)(3, "Liberar cupo")
        val creada = solicitar(repo)("medicina-general", "nana", "2026-10-12", "09:00",
            "Consulta médica general", ModalidadAtencion.Teleconsulta)
        assertEquals(ModalidadAtencion.Teleconsulta, repo.obtenerCita(creada.id)?.modalidad)
    }

    @Test
    fun `SC-D reprograma sin cambiar id ni duplicar horario`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val reprogramar = ReprogramarCitaUseCase(repo, ValidarHorarioCita(reloj))
        val cita = reprogramar(1, "2026-10-15", "10:30")
        assertEquals(1L, cita.id)
        assertEquals(LocalDate(2026, 10, 15), cita.fecha)
        assertEquals(3, CupoCitasUseCase(repo).cantidadProgramadas(repo.listarCitas()))
        val ocupada = requireNotNull(repo.obtenerCita(2))
        val error = errorDe {
            reprogramar(1, ocupada.fecha.toString(), ocupada.hora.toString())
        }
        assertTrue("hora" in error.errores)
    }

    @Test
    fun `SC-D no reprograma una cita atendida`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val reprogramar = ReprogramarCitaUseCase(repo, ValidarHorarioCita(reloj))
        try {
            reprogramar(4, "2026-10-15", "10:30")
            throw AssertionError("Debió rechazar la cita atendida")
        } catch (_: ReglaCitaException) { }
    }

    @Test
    fun `SC-D rechaza reprogramar para una fecha pasada`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val error = errorDe {
            ReprogramarCitaUseCase(repo, ValidarHorarioCita(reloj))(1, "2026-09-21", "09:00")
        }
        assertTrue("fecha" in error.errores)
    }

    @Test
    fun `la lista muestra la proxima cita antes del historial`() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val citas = ObtenerCitasUseCase(repo, reloj)()
        assertEquals(LocalDate(2026, 9, 24), citas.first().fecha)
        assertTrue(citas.last().estado !is EstadoCita.Programada)
    }

    private suspend fun errorDe(block: suspend () -> Unit): CitaInvalidaException = try {
        block()
        throw AssertionError("Se esperaba una validación")
    } catch (error: CitaInvalidaException) {
        error
    }

    private fun solicitar(repo: CitaRepositoryFake) =
        SolicitarCitaUseCase(repo, ValidarHorarioCita(reloj), CupoCitasUseCase(repo))
}
