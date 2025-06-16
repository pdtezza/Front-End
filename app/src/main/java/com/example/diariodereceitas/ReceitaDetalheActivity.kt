package com.example.diariodereceitas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.diariodereceitas.databinding.ActivityReceitaDetalheBinding

class ReceitaDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaDetalheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceitaDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados enviados pela Intent
        val fotoUrl = intent.getStringExtra("fotoUrl")
        val titulo = intent.getStringExtra("titulo") ?: ""
        val autor = intent.getStringExtra("autor") ?: ""
        val ingredientes = intent.getStringArrayListExtra("ingredientes")
        val modoPreparo = intent.getStringArrayListExtra("modoPreparo")
        val dicas = intent.getStringArrayListExtra("dicas")

        // Carrega a imagem
        Glide.with(this)
            .load(fotoUrl)
            .placeholder(R.drawable.bololimao)
            .into(binding.imgReceita)

        binding.txtTitulo.text = titulo
        // Mostra @autor
        binding.txtAutor.text = if (autor.isNotBlank()) "@$autor" else ""

        binding.txtIngredientes.text = ingredientes?.joinToString("\n") ?: ""
        binding.txtModoPreparo.text = modoPreparo?.joinToString("\n") ?: ""
        binding.txtDicas.text = dicas?.joinToString("\n") ?: ""

        binding.btnVoltar.setOnClickListener { finish() }
    }
}
