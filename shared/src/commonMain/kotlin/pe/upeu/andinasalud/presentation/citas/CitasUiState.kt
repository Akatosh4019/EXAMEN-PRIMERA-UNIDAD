package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.presentation.common.CitaUi
import pe.upeu.andinasalud.presentation.common.FiltroCitas

sealed interface FaseCitas {
    data object Cargando : FaseCitas
    data object Vacia : FaseCitas
    data class Contenido(val citas: List<CitaUi>) : FaseCitas
    data class Error(val mensaje: String) : FaseCitas
}

data class CitasUiState(
    val fase: FaseCitas = FaseCitas.Cargando,
    val filtro: FiltroCitas = FiltroCitas.Todas,
    val busqueda: String = "",
    val soloHoy: Boolean = false,
)
