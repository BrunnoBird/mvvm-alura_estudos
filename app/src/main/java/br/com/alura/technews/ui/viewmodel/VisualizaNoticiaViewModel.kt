package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

class VisualizaNoticiaViewModel(
    private val repository: NoticiaRepository,
    id: Long
) : ViewModel() {
    //pegas as mudanças que teve e armazena
     val noticiaEncontrada = repository.buscaPorId(id)

    fun remove(): LiveData<Resource<Void?>> {
        /* run -> pega o valor garantido que não é null e envolvo dentro da função a ser executada
        *  also -> pega o valor que estou avaliando e executo algum codigo dentro.
        */
        //verificando se a noticiaEncontrada é null ou não! se for certo executo o RUN
        return noticiaEncontrada.value?.run {
            repository.remove(this)
        }
            ?: //elvis expression -> Operador ternário, avalia toda a expressão acima e se verificar que em um momento ele é null ele vai para a segunda execução do ALSO
            MutableLiveData<Resource<Void?>>().also {
                //Quando da um valor null executo este código
                it.value = Resource(null, "Notícia não encontrada")
            }
    }
}