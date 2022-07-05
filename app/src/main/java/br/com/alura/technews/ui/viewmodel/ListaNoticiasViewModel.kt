package br.com.alura.technews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository

class ListaNoticiasViewModel(private val repository: NoticiaRepository) : ViewModel() {

    fun buscaTodos(quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit, quandoFalha: (erro: String?) -> Unit) {
        repository.buscaTodos(quandoSucesso, quandoFalha)
    }

    //Factory para usarmos uma ViewModel com parametrôs de repositorio
    class ListaNoticiasViewModelFactory(
        private val repository: NoticiaRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ListaNoticiasViewModel(repository) as T;
        }

    }
}