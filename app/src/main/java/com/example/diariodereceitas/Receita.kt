package com.example.diariodereceitas

data class Receita(
    val id: String,
    val nome: String,
    val imagemUrl: String?,
    val likes: Int,
    val visualizacoes: Int,
    val autor: String,
    val autorId: String,
    val ingredientes: String,
    val modoPreparo: String,
    val dicas: String,
    val privado: Boolean
)
