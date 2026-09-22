package pe.upeu.andinasalud.domain.usecase

class CitaInvalidaException(val errores: Map<String, String>) :
    IllegalArgumentException(errores.values.firstOrNull() ?: "La cita no es válida")

class ReglaCitaException(message: String) : IllegalStateException(message)
