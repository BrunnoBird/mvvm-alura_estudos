package br.com.alura.technews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia

private const val NOME_BANCO_DE_DADOS = "news.db"

@Database(entities = [Noticia::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val noticiaDAO: NoticiaDAO

    //Singleton para que todas as viewmodels tenha a mesma referencia do banco
    companion object {

        private lateinit var db: AppDatabase

        fun getInstance(context: Context): AppDatabase {

            //verificando se nosso DB foi ininicializado
            //::db.isInitialized -> função reflection do lateinit para ver se já foi inicializado
            if (::db.isInitialized) return db

            //Caso não foi inicializado vamos criar a instancia
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                NOME_BANCO_DE_DADOS
            ).build()

            return db
        }

    }

}