package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository

class ListaNoticiasViewModel(private val repository: NoticiaRepository) : ViewModel() {
    fun buscaTodos(): LiveData<List<Noticia>> {
        //Live Data -> é retornado do meu repository
        return repository.buscaTodos()
    }
}