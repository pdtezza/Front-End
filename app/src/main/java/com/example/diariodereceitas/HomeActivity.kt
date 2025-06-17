package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityHomeBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val receitas = mutableListOf<Receita>()
    private var favoritosSet: Set<String> = emptySet()
    private lateinit var adapter: ReceitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = ReceitaAdapter(receitas, favoritosSet)
        binding.recyclerViewReceitas.adapter = adapter

        binding.bottomBar.findViewById<View>(R.id.button_nav_add).setOnClickListener {
            startActivity(Intent(this, AddReceitaActivity::class.java))
        }

        binding.bottomBar.findViewById<View>(R.id.button_nav_favorites).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        binding.bottomBar.findViewById<View>(R.id.button_nav_search).setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_profile).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
            finish()
        }

        binding.swipeRefresh.setOnRefreshListener {
            atualizarReceitas()
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarReceitas()
    }

    private fun atualizarReceitas() {
        binding.progressBar.visibility = View.VISIBLE
        buscarFavoritosUsuario { favoritos ->
            favoritosSet = favoritos
            adapter.favoritosSet = favoritosSet
            buscarReceitas()
        }
    }

    fun atualizarFavoritosSomente() {
        buscarFavoritosUsuario { favoritos ->
            favoritosSet = favoritos
            adapter.favoritosSet = favoritosSet
            adapter.notifyDataSetChanged()
        }
    }

    private fun buscarFavoritosUsuario(onResult: (Set<String>) -> Unit) {
        val usuarioId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
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
                    val respStr = response.body?.string()
                    val jsonArray = JSONArray(respStr)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val id = obj.optString("id", null)
                        if (id != null) {
                            set.add(id)
                        }
                    }
                    runOnUiThread {
                        onResult(set)
                    }
                } else {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        onResult(emptySet())
                    }
                }
            }
        })
    }

    private fun buscarReceitas() {
        val url = "${ApiConfig.BASE_URL}/receitas/populares"
        val request = Request.Builder().url(url).get().build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@HomeActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val respStr = response.body?.string()
                        val listaReceitas = mutableListOf<Receita>()
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
                                autorId = obj.optString("autorId", ""),
                                ingredientes = obj.optString("ingredientes", ""),
                                modoPreparo = obj.optString("modoPreparo", ""),
                                dicas = obj.optString("dicas", ""),
                                privado = obj.optBoolean("privado", false)
                            )
                            listaReceitas.add(receita)
                        }
                        receitas.clear()
                        receitas.addAll(listaReceitas)
                        adapter.favoritosSet = favoritosSet
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@HomeActivity, "Erro ao buscar receitas: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        })
    }
}
