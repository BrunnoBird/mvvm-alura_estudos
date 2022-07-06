package br.com.alura.technews

import android.app.Application
import br.com.alura.technews.di.modules.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppAplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //Indicamos o contexto do android
            androidContext(this@AppAplication) //estamos colocando essa referencia aqui como contexto de android
            //adicionando os modulos, que seria nosso appModules
            modules(appModules)
        }
    }
}