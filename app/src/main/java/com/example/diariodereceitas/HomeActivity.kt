package com.example.diariodereceitas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diariodereceitas.databinding.ActivityHomeBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val receitas = mutableListOf<Receita>()
    private lateinit var adapter: ReceitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ReceitaAdapter(receitas)
        binding.recyclerViewReceitas.adapter = adapter
        binding.recyclerViewReceitas.layoutManager = LinearLayoutManager(this)

        buscarReceitas()
    }

    private fun buscarReceitas() {
        val url = "${ApiConfig.BASE_URL}/receitas"
        val request = Request.Builder().url(url).get().build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Erro ao buscar receitas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val listaReceitas = mutableListOf<Receita>()
                if (response.isSuccessful) {
                    val respStr = response.body?.string()
                    val jsonArray = JSONArray(respStr)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val receita = Receita(
                            id = obj.getString("id"),
                            nome = obj.getString("nome"),
                            imagemUrl = obj.optString("imagemUrl", null),
                            likes = obj.getInt("likes"),
                            visualizacoes = obj.getInt("visualizacoes"),
                            autor = obj.getString("autor")
                        )
                        listaReceitas.add(receita)
                    }
                }
                runOnUiThread {
                    receitas.clear()
                    receitas.addAll(listaReceitas)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}
