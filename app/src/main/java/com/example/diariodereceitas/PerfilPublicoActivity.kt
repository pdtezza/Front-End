package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityPerfilPublicoBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class PerfilPublicoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilPublicoBinding
    private val receitas = mutableListOf<Receita>()
    private var favoritosSet: Set<String> = emptySet()
    private lateinit var adapter: ReceitaAdapter
    private var usuarioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilPublicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioId = intent.getStringExtra("usuarioId") ?: ""

        binding.recyclerViewUserReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = ReceitaAdapter(receitas, favoritosSet)
        binding.recyclerViewUserReceitas.adapter = adapter

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.bottomBar.findViewById<View>(R.id.button_nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_search).setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_favorites).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_profile).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
            finish()
        }

        binding.progressBar.visibility = View.VISIBLE
        buscarFavoritosUsuario {
            favoritosSet = it
            adapter.favoritosSet = favoritosSet
            carregarDadosDoUsuario()
        }
    }

    private fun buscarFavoritosUsuario(onResult: (Set<String>) -> Unit) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val usuarioLogadoId = sharedPref.getString("usuarioId", "") ?: ""
        if (usuarioLogadoId.isEmpty()) {
            onResult(emptySet())
            return
        }

        val url = "${ApiConfig.BASE_URL}/clientes/favoritas?clienteId=$usuarioLogadoId"
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

    private fun carregarDadosDoUsuario() {
        val url = "${ApiConfig.BASE_URL}/clientes/$usuarioId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PerfilPublicoActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonObj = JSONObject(response.body?.string())
                    val nome = jsonObj.optString("nome", "Usuário")
                    val primeiraLetra = nome.firstOrNull()?.toString()?.uppercase() ?: "?"

                    runOnUiThread {
                        binding.usernameText.text = nome
                        binding.userInitial.text = primeiraLetra
                    }

                    buscarReceitasDoUsuario()
                } else {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@PerfilPublicoActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun buscarReceitasDoUsuario() {
        val url = "${ApiConfig.BASE_URL}/receitas/autor/$usuarioId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PerfilPublicoActivity, "Erro ao carregar receitas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
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
                        binding.progressBar.visibility = View.GONE
                        receitas.clear()
                        receitas.addAll(novasReceitas)
                        adapter.favoritosSet = favoritosSet
                        adapter.notifyDataSetChanged()
                        binding.textQtdReceitas.text = "${receitas.size} receitas publicadas"
                    }
                } else {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@PerfilPublicoActivity, "Erro ao carregar receitas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
