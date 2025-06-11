package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnBoloLimao = findViewById<ImageButton>(R.id.btnBoloLimao)
        val btnBoloLaranja = findViewById<ImageButton>(R.id.btnBoloLaranja)
        val btnBrigadeiroTrufado = findViewById<ImageButton>(R.id.btnBrigadeirotrufado)
        val btnLasanha = findViewById<ImageButton>(R.id.btnLasanha)

        // Clique no botão de Bolo de Limão
        btnBoloLimao.setOnClickListener {
            abrirTelaReceita("Bolo de Limão")
        }

        btnBoloLaranja.setOnClickListener {
            abrirTelaReceita("Bolo de Laranja")
        }

        btnBrigadeiroTrufado.setOnClickListener {
            abrirTelaReceita("Brigadeiro Trufado")
        }

        btnLasanha.setOnClickListener {
            abrirTelaReceita("Lasanha")
        }
    }

    private fun abrirTelaReceita(nome: String) {
        val intent = Intent(this, ReceitaActivity::class.java)
        intent.putExtra("nomeReceita", nome)
        startActivity(intent)
    }
}
