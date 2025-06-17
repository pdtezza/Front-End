package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diariodereceitas.databinding.ActivityPesquisaBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.net.URLEncoder

class PesquisaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesquisaBinding
    private val receitas = mutableListOf<Receita>()
    private var favoritosSet: Set<String> = emptySet()
    private lateinit var adapter: ReceitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesquisaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewReceitas.layoutManager = GridLayoutManager(this, 2)
        adapter = ReceitaAdapter(receitas, favoritosSet)
        binding.recyclerViewReceitas.adapter = adapter

        binding.bottomBar.findViewById<View>(R.id.button_nav_search).setOnClickListener {
            Toast.makeText(this, "Você já está na tela de pesquisa!", Toast.LENGTH_SHORT).show()
        }

        binding.bottomBar.findViewById<View>(R.id.button_nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.bottomBar.findViewById<View>(R.id.button_nav_favorites).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.bottomBar.findViewById<View>(R.id.button_nav_profile).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
            finish()
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val termo = s.toString()
                if (termo.length >= 2) {
                    binding.progressBar.visibility = View.VISIBLE
                    buscarFavoritosUsuario {
                        favoritosSet = it
                        adapter.favoritosSet = favoritosSet
                        buscarReceitasPorTitulo(termo)
                    }
                } else {
                    receitas.clear()
                    adapter.notifyDataSetChanged()
                }
            }
        })
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

    private fun buscarReceitasPorTitulo(titulo: String) {
        val url = "${ApiConfig.BASE_URL}/receitas/pesquisar?titulo=" + URLEncoder.encode(titulo, "UTF-8")
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PesquisaActivity, "Erro ao buscar receitas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        Toast.makeText(this@PesquisaActivity, "Nenhuma receita encontrada", Toast.LENGTH_SHORT).show()
                        receitas.clear()
                        adapter.notifyDataSetChanged()
                        return@runOnUiThread
                    }

                    val jsonArray = JSONArray(responseBody)
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

                    receitas.clear()
                    receitas.addAll(novasReceitas)
                    adapter.favoritosSet = favoritosSet
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}
