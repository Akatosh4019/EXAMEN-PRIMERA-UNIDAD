package pe.upeu.andinasalud.domain.model

data class Medico(
    val id: String,
    val nombre: String,
    val especialidadId: String,
    val sedeIds: Set<String>,
)
