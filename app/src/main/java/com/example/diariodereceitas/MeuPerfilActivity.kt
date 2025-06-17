package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityMeuPerfilBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MeuPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeuPerfilBinding
    private val receitasPublicas = mutableListOf<Receita>()
    private lateinit var adapter: MeuPerfilAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeuPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewMinhasReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = MeuPerfilAdapter(this, receitasPublicas)
        binding.recyclerViewMinhasReceitas.adapter = adapter

        // BottomBar Navegação
        binding.bottomBar.findViewById<View>(R.id.button_nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_search).setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_add).setOnClickListener {
            startActivity(Intent(this, AddReceitaActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_favorites).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }


        binding.progressBar.visibility = View.VISIBLE
        carregarDadosDoUsuario()
    }

    private fun carregarDadosDoUsuario() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val usuarioId = sharedPref.getString("usuarioId", "") ?: ""

        if (usuarioId.isEmpty()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Usuário não identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiConfig.BASE_URL}/receitas/autor/$usuarioId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MeuPerfilActivity, "Erro ao carregar receitas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread { binding.progressBar.visibility = View.GONE }

                if (response.isSuccessful) {
                    val jsonArray = JSONArray(response.body?.string())
                    val novasReceitas = mutableListOf<Receita>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val receita = Receita(
                            id = obj.optString("id", ""),
                            nome = obj.optString("titulo", ""),
                            imagemUrl = obj.optString("fotoUrl", null),
                            likes = obj.optInt("likes", 0),
                            visualizacoes = obj.optInt("visualizacoes", 0),
                            autor = obj.optString("usuarioNome", ""),
                            autorId = obj.optString("autorId", ""),
                            ingredientes = obj.optString("ingredientes", ""),
                            modoPreparo = obj.optString("modoPreparo", ""),
                            dicas = obj.optString("dicas", ""),
                            privado = obj.optBoolean("privado", false)
                        )

                        // Só adicionar as públicas
                        if (!receita.privado) {
                            novasReceitas.add(receita)
                        }
                    }

                    runOnUiThread {
                        receitasPublicas.clear()
                        receitasPublicas.addAll(novasReceitas)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
