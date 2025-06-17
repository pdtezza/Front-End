package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.diariodereceitas.databinding.ActivityReceitaDetalheBinding

class ReceitaDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceitaDetalheBinding
    private var autorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceitaDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titulo = intent.getStringExtra("titulo")
        val autor = intent.getStringExtra("autor")
        val fotoUrl = intent.getStringExtra("fotoUrl")
        autorId = intent.getStringExtra("usuarioIdAutor")  // Pegando o autorId vindo do Adapter

        val ingredientes = intent.getStringArrayListExtra("ingredientes") ?: arrayListOf()
        val modoPreparo = intent.getStringArrayListExtra("modoPreparo") ?: arrayListOf()
        val dicas = intent.getStringArrayListExtra("dicas") ?: arrayListOf()

        // Preenche os campos da tela
        binding.txtTitulo.text = titulo
        binding.txtAutor.text = autor

        Glide.with(this)
            .load(fotoUrl)
            .placeholder(R.drawable.bololimao)
            .into(binding.imgReceita)

        binding.txtIngredientes.text = ingredientes.joinToString("\n")
        binding.txtModoPreparo.text = modoPreparo.joinToString("\n")
        binding.txtDicas.text = dicas.joinToString("\n")

        // Clique no nome do autor → Abrir o perfil público
        binding.txtAutor.setOnClickListener {
            if (autorId.isNullOrEmpty()) {
                Toast.makeText(this, "ID do autor não disponível!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, PerfilPublicoActivity::class.java)
                intent.putExtra("usuarioId", autorId)
                startActivity(intent)
            }
        }

        // Botão voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }
}
