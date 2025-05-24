package com.example.teste

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


import android.widget.EditText

class ReceitaActivity : AppCompatActivity() {


    private lateinit var edtReceita: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receita)


        edtReceita = findViewById(R.id.edtReceita)

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
            val receitaFinal = edtReceita.text.toString()
            if (receitaFinal.isNotBlank()) {

                Toast.makeText(this, "Receita salva: $receitaFinal", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, escreva sua receita.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}