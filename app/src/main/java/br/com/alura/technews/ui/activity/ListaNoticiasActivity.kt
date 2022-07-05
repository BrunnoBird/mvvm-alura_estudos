package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import br.com.alura.technews.R
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.FalhaResource
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.SucessoResource
import br.com.alura.technews.ui.activity.extensions.mostraErro
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import br.com.alura.technews.ui.viewmodel.factory.ListaNoticiasViewModelFactory
import kotlinx.android.synthetic.main.activity_lista_noticias.*

private const val TITULO_APPBAR = "Notícias"
private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Não foi possível carregar as novas notícias"

class ListaNoticiasActivity : AppCompatActivity() {
    //View model como prop para que todos possam acessa-la
    private val viewModel by lazy {
        val repository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
        val factory = ListaNoticiasViewModelFactory(repository)
        ViewModelProvider(
            this,
            factory
        ).get(ListaNoticiasViewModel::class.java)
    }

    private val adapter by lazy {
        ListaNoticiasAdapter(context = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_noticias)
        title = TITULO_APPBAR
        configuraRecyclerView()
        configuraFabAdicionaNoticia()
        buscaNoticias()
    }

    private fun configuraFabAdicionaNoticia() {
        activity_lista_noticias_fab_salva_noticia.setOnClickListener {
            abreFormularioModoCriacao()
        }
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(this, VERTICAL)
        activity_lista_noticias_recyclerview.addItemDecoration(divisor)
        activity_lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
        adapter.quandoItemClicado = this::abreVisualizadorNoticia
    }

    private fun buscaNoticias() {
        //observe -> sempre observa o liveData ver se tem algo novo
        viewModel.buscaTodos().observe(this, Observer { resource ->
            //Quando tiver ativo ele vai chamar quando não estiver vai perder o vinculo.
            resource.dado?.let {
                //se não for nulo vamos atualizar o adapter
                adapter.atualiza(it)
            }
            //Posso fazer deste modo tambem
            when (resource) {
                is SucessoResource -> {
                    //executa procedimento de sucesso
                }
                is FalhaResource -> {
                    //executa procedimento de falha
                    mostraErro(MENSAGEM_FALHA_CARREGAR_NOTICIAS)
                }
            }
        })

    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(it: Noticia) {
        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, it.id)
        startActivity(intent)
    }

}
