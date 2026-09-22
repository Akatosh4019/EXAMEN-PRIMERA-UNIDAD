package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.upeu.andinasalud.data.local.RelojDelSistema
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.CupoCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val appModule = module {
    single<RelojClinico> { RelojDelSistema() }
    single<CitaRepository> { CitaRepositoryFake(get()) }
    factory { ObtenerCitasUseCase(get(), get()) }
    factory { ObtenerCitaUseCase(get()) }
    factory { ObtenerPacienteUseCase(get()) }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { CupoCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get(), get(), get()) }
    factory { CancelarCitaUseCase(get(), get()) }
    viewModel { InicioViewModel(get(), get(), get()) }
    viewModel { CitasViewModel(get(), get(), get(), get()) }
    viewModel { DetalleCitaViewModel(get(), get(), get()) }
    viewModel { SolicitudViewModel(get(), get()) }
    viewModel { PerfilViewModel(get()) }
}

fun initKoin() = startKoin { modules(appModule) }
