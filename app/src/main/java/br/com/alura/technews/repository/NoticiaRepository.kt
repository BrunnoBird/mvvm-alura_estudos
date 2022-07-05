package br.com.alura.technews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {
    /* Property para que nosso liveData tenha sempre em memória os valores disponíveis
    acesso a leitura e escrita no MutableLiveData */
    private val noticiasEncontradas = MutableLiveData<Resource<List<Noticia>?>>()

    //LiveData do Repository
    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> {
        val atualizaListaNoticias: (List<Noticia>) -> Unit = {
            noticiasEncontradas.value = Resource(dado = it)
        }
        buscaInterno(quandoSucesso = atualizaListaNoticias)
        buscaNaApi(quandoSucesso = atualizaListaNoticias, quandoFalha = { erro ->
            val resourceAtual = noticiasEncontradas.value
            val resourceDeFalha = criaResourceDeFalha<List<Noticia>?>(resourceAtual, erro)
            noticiasEncontradas.value = resourceDeFalha
        })
        return noticiasEncontradas
    }

    fun salva(
        noticia: Noticia
    ): LiveData<Resource<Void?>> {
        //não precisamos criar um properti pois não queremos cachear nosso estado Livedata
        val liveData = MutableLiveData<Resource<Void?>>()

        salvaNaApi(noticia, quandoSucesso = {
            liveData.value = Resource(null) //não preciso armazenar o valor
        }, quandoFalha = { erro ->
            liveData.value = Resource(null, erro)
        })
        return liveData
    }

    fun remove(noticia: Noticia): LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()

        removeNaApi(noticia, quandoSucesso = {
            liveData.value = Resource(null)
        }, quandoFalha = { erro ->
            liveData.value = Resource(null, erro = erro)
        })
        return liveData
    }

    fun edita(
        noticia: Noticia
    ): LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()
        editaNaApi(noticia, quandoSucesso = {
            liveData.value = Resource(null)
        }, quandoFalha = { erro ->
            liveData.value = Resource(dado = null, erro = erro)
        })
        return liveData
    }

    fun buscaPorId(
        noticiaId: Long
    ): LiveData<Noticia?> {
        /*Não precisamos de RESOURCE pois,
        * Como não precisamos de um retorno da API podemos apenas retornar o dado direto
        * ou caso não tiver dado(null), subentendemos que esse dado apenas não existe
        */
        val liveData = MutableLiveData<Noticia?>()

        BaseAsyncTask(quandoExecuta = {
            dao.buscaPorId(noticiaId)
        }, quandoFinaliza = { noticia ->
            liveData.value = noticia
        }).execute()
        return liveData
    }

    private fun buscaNaApi(
        quandoSucesso: (List<Noticia>) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    salvaInterno(noticiasNovas, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(quandoSucesso: (List<Noticia>) -> Unit) {
        BaseAsyncTask(quandoExecuta = {
            dao.buscaTodos()
        }, quandoFinaliza = quandoSucesso)
            .execute()
    }


    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(noticiaSalva, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(
        noticias: List<Noticia>,
        quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
                dao.buscaTodos()
            }, quandoFinaliza = quandoSucesso
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
            dao.buscaPorId(noticia.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                quandoSucesso(it)
            }
        }).execute()

    }

    private fun removeNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = {
                removeInterno(noticia, quandoSucesso)
            },
            quandoFalha = quandoFalha
        )
    }


    private fun removeInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.remove(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaEditada: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.edita(
            noticia.id, noticia,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let {
                    salvaInterno(noticiaEditada, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

}
