package com.example.teste

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class AddReceitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_receita_activity)

        findViewById<CardView>(R.id.card_bolo_limao).setOnClickListener {
            val intent = Intent(this, ReceitaActivity::class.java)
            intent.putExtra("receitaNome", "Bolo de Limão")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.card_bolo_laranja).setOnClickListener {
            val intent = Intent(this, ReceitaActivity::class.java)
            intent.putExtra("receitaNome", "Bolo de Laranja")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.card_brigadeiro_trufado).setOnClickListener {
            val intent = Intent(this, ReceitaActivity::class.java)
            intent.putExtra("receitaNome", "Brigadeiro Trufado")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.card_lasanha).setOnClickListener {
            val intent = Intent(this, ReceitaActivity::class.java)
            intent.putExtra("receitaNome", "Lasanha")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.card_receitas_privadas).setOnClickListener {
            val intent = Intent(this, ReceitaPrivadaActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.button_nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.button_nav_search).setOnClickListener {

        }

        findViewById<ImageButton>(R.id.button_nav_add).setOnClickListener {

        }

        findViewById<ImageButton>(R.id.button_nav_favorites).setOnClickListener {

        }

        findViewById<ImageButton>(R.id.button_nav_profile).setOnClickListener {

        }

        findViewById<ImageButton>(R.id.buttonIcon).setOnClickListener {

        }
    }
}