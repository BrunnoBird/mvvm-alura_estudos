package br.com.alura.technews.di.modules

import androidx.room.Room
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient
import br.com.alura.technews.ui.viewmodel.FormularioNoticiaViewModel
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import br.com.alura.technews.ui.viewmodel.VisualizaNoticiaViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val NOME_BANCO_DE_DADOS = "news.db"

val appModules = module {
    //Onde indico todas as dependências que o KOIN saiba criar e que injete para nós
    factory {
        //Qualquer tipo de dependencia que for Factory sempre será uma nova instancia
    }

    single<AppDatabase> {
        //single -> singleton, sempre uma unica instancia
        Room.databaseBuilder(
            get(), //get -> Koin tenta de alguma maneira injetar essa dependência que ele precisa
            AppDatabase::class.java,
            NOME_BANCO_DE_DADOS
        ).build()
    }

    single<NoticiaDAO> {
        get<AppDatabase>().noticiaDAO
    }

    single<NoticiaWebClient> {
        NoticiaWebClient()
    }

    single<NoticiaRepository> {
        NoticiaRepository(get(), get())
    }

    viewModel<ListaNoticiasViewModel> {
        ListaNoticiasViewModel(get())
    }
    viewModel<FormularioNoticiaViewModel> {
        FormularioNoticiaViewModel(get())
    }
    viewModel<VisualizaNoticiaViewModel> { (id: Long) ->
        VisualizaNoticiaViewModel(get(), id)
    }
}