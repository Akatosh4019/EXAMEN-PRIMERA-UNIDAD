package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.dsl.module
import pe.upeu.andinasalud.data.local.RelojDelSistema
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.domain.usecase.RelojClinico
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase

val appModule = module {
    single<RelojClinico> { RelojDelSistema() }
    single<CitaRepository> { CitaRepositoryFake(get()) }
    factory { ObtenerCitasUseCase(get()) }
    factory { ObtenerCitaUseCase(get()) }
    factory { ObtenerPacienteUseCase(get()) }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { SolicitarCitaUseCase(get(), get()) }
    factory { CancelarCitaUseCase(get(), get()) }
}

fun initKoin() = startKoin { modules(appModule) }
