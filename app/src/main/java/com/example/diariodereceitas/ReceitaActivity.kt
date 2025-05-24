package com.diariodereceitas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReceitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receita)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnEdit = findViewById<ImageButton>(R.id.btnEdit)
        val btnShare = findViewById<ImageButton>(R.id.btnShare)
        val switchPrivado = findViewById<Switch>(R.id.switchPrivado)
        val btnConcluir = findViewById<Button>(R.id.btnConcluir)

        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            Toast.makeText(this, "Editar receita", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
            Toast.makeText(this, "Compartilhar receita", Toast.LENGTH_SHORT).show()
        }

        btnConcluir.setOnClickListener {
            Toast.makeText(this, "Receita salva!", Toast.LENGTH_SHORT).show()
        }
    }
}

}