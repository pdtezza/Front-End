package com.example.diariodereceitas

data class Receita(
    val id: String,
    val nome: String,
    val imagemUrl: String?, // ou só String se sempre tiver imagem
    val likes: Int,
    val visualizacoes: Int,
    val autor: String
)
