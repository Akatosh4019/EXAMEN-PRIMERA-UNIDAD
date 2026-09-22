package pe.upeu.andinasalud.presentation.common

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.CatalogoClinico

enum class FiltroCitas(val etiqueta: String) {
    Todas("Todas"), Programadas("Programadas"), Atendidas("Atendidas"), Canceladas("Canceladas")
}

data class CitaUi(
    val id: Long,
    val especialidad: String,
    val medico: String,
    val sede: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val filtro: FiltroCitas,
    val motivo: String,
    val indicaciones: String?,
)

fun Cita.aUi(catalogo: CatalogoClinico): CitaUi {
    val (estadoTexto, filtroCita, indicaciones) = when (val valor = estado) {
        is EstadoCita.Programada -> Triple("Programada", FiltroCitas.Programadas, null)
        is EstadoCita.Atendida -> Triple("Atendida", FiltroCitas.Atendidas, valor.indicaciones)
        is EstadoCita.Cancelada -> Triple("Cancelada", FiltroCitas.Canceladas, valor.motivo)
    }
    return CitaUi(
        id = id,
        especialidad = catalogo.especialidades.firstOrNull { it.id == especialidadId }?.nombre
            ?: especialidadId,
        medico = catalogo.medicos.firstOrNull { it.id == medicoId }?.nombre ?: medicoId,
        sede = catalogo.sedes.firstOrNull { it.id == sedeId }?.nombre ?: sedeId,
        fecha = fecha.toString(),
        hora = hora.toString(),
        estado = estadoTexto,
        filtro = filtroCita,
        motivo = motivo,
        indicaciones = indicaciones,
    )
}

fun String.sinTildes(): String = lowercase()
    .replace('á', 'a').replace('é', 'e').replace('í', 'i')
    .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
