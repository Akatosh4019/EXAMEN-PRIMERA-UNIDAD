package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.repository.CitaRepository

data class CatalogoClinico(
    val sedes: List<Sede>,
    val especialidades: List<Especialidad>,
    val medicos: List<Medico>,
)

class ObtenerCatalogoUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke() = CatalogoClinico(
        sedes = repositorio.sedes(),
        especialidades = repositorio.especialidades(),
        medicos = repositorio.medicos(),
    )
}
