package br.com.alura.technews.repository

//Classe que ela sabe lhe dar com qualquer tipo de dados passados para o no nosso LiveData
open class Resource<T>(
    val dado: T?,
    val erro: String? = null
)

class SucessoResource<T>(dado: T) : Resource<T>(dado)

class FalhaResource<T>(erro: String) : Resource<T>(dado = null, erro = erro)