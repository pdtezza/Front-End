package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityMinhasPrivadasBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MinhasPrivadasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinhasPrivadasBinding
    private val receitas = mutableListOf<Receita>()
    private lateinit var adapter: ReceitaAdapter
    private var favoritosSet: Set<String> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinhasPrivadasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = ReceitaAdapter(receitas, favoritosSet, mostrarLike = false)  // <<<<<< ALTERADO AQUI
        binding.recyclerViewReceitas.adapter = adapter

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
        binding.bottomBar.findViewById<View>(R.id.button_nav_profile).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }

        binding.progressBar.visibility = View.VISIBLE
        buscarFavoritosUsuario {
            favoritosSet = it
            adapter.favoritosSet = favoritosSet
            carregarPrivadasDoUsuario()
        }
    }

    private fun buscarFavoritosUsuario(onResult: (Set<String>) -> Unit) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val usuarioId = sharedPref.getString("usuarioId", "") ?: ""

        if (usuarioId.isEmpty()) {
            onResult(emptySet())
            return
        }

        val url = "${ApiConfig.BASE_URL}/clientes/favoritas?clienteId=$usuarioId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    onResult(emptySet())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val set = mutableSetOf<String>()
                    val jsonArray = JSONArray(response.body?.string())
                    for (i in 0 until jsonArray.length()) {
                        set.add(jsonArray.getString(i))
                    }
                    runOnUiThread { onResult(set) }
                } else {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        onResult(emptySet())
                    }
                }
            }
        })
    }

    private fun carregarPrivadasDoUsuario() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val usuarioId = sharedPref.getString("usuarioId", "") ?: ""

        if (usuarioId.isEmpty()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Usuário não identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiConfig.BASE_URL}/receitas/minhasPrivadas?clienteId=$usuarioId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MinhasPrivadasActivity, "Erro ao carregar privadas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }

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
                        novasReceitas.add(receita)
                    }

                    runOnUiThread {
                        receitas.clear()
                        receitas.addAll(novasReceitas)
                        adapter.favoritosSet = favoritosSet
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
