package com.example.diariodereceitas

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReceitaServiceTest {

    @Test
    fun `deve retornar o total correto de receitas`() {
        val service = ReceitaService()
        val receitas = listOf("Bolo", "Pizza", "Lasanha")

        val total = service.calcularTotal(receitas)

        assertEquals(3, total)
    }

    @Test
    fun `deve retornar zero quando lista estiver vazia`() {
        val service = ReceitaService()
        val receitas = emptyList<String>()

        val total = service.calcularTotal(receitas)

        assertEquals(0, total)
    }
}
