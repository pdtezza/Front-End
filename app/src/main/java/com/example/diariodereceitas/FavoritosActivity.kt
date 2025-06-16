package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityHomeBinding // Reutilize o layout da Home
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class FavoritosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val receitas = mutableListOf<Receita>()
    private lateinit var adapter: ReceitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = ReceitaAdapter(receitas, setOf()) // O set será atualizado depois
        binding.recyclerViewReceitas.adapter = adapter

        binding.bottomBar.findViewById<View>(R.id.button_nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        buscarFavoritos()
    }

    private fun buscarFavoritos() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) {
            Toast.makeText(this, "Faça login!", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Busca ids das receitas favoritas
        val urlFav = "${ApiConfig.BASE_URL}/clientes/favoritos?clienteId=$usuarioId"
        OkHttpClient().newCall(Request.Builder().url(urlFav).get().build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@FavoritosActivity, "Erro ao buscar favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@FavoritosActivity, "Erro: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val idsStr = response.body?.string() ?: "[]"
                    val idsArray = JSONArray(idsStr)
                    val favoritosIds = mutableSetOf<String>()
                    for (i in 0 until idsArray.length()) {
                        favoritosIds.add(idsArray.getString(i))
                    }

                    // 2. Busca todas receitas populares
                    buscarTodasReceitas(favoritosIds)
                }
            })
    }

    // Busca todas as receitas, depois filtra só as favoritas
    private fun buscarTodasReceitas(favoritosIds: Set<String>) {
        val url = "${ApiConfig.BASE_URL}/receitas/populares"
        OkHttpClient().newCall(Request.Builder().url(url).get().build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@FavoritosActivity, "Erro ao buscar receitas: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: Call, response: Response) {
                    val receitasLista = mutableListOf<Receita>()
                    if (response.isSuccessful) {
                        val respStr = response.body?.string()
                        val jsonArray = JSONArray(respStr)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val receita = Receita(
                                id = obj.optString("id", ""),
                                nome = obj.optString("titulo", ""),
                                imagemUrl = obj.optString("fotoUrl", null),
                                likes = obj.optInt("likes", 0),
                                visualizacoes = obj.optInt("visualizacoes", 0),
                                autor = obj.optString("usuarioNome", ""),
                                ingredientes = obj.optString("ingredientes", ""),
                                modoPreparo = obj.optString("modoPreparo", ""),
                                dicas = obj.optString("dicas", "")
                            )
                            // Só adiciona se estiver nos favoritos
                            if (favoritosIds.contains(receita.id)) {
                                receitasLista.add(receita)
                            }
                        }
                    }
                    runOnUiThread {
                        adapter.favoritosSet = favoritosIds
                        receitas.clear()
                        receitas.addAll(receitasLista)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }
}
