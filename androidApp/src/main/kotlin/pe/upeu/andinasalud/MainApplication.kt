package pe.upeu.andinasalud

import android.app.Application
import pe.upeu.andinasalud.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
