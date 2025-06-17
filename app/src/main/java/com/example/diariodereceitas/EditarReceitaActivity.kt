package com.example.diariodereceitas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diariodereceitas.databinding.ActivityEditarReceitaBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EditarReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarReceitaBinding
    private var receitaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receitaId = intent.getStringExtra("receitaId") ?: ""

        if (receitaId.isEmpty()) {
            Toast.makeText(this, "ID da receita não encontrado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        carregarDadosDaReceita()

        binding.btnSalvar.setOnClickListener {
            atualizarReceita()
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun carregarDadosDaReceita() {
        val url = "${ApiConfig.BASE_URL}/receitas/$receitaId"
        val request = Request.Builder().url(url).get().build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditarReceitaActivity, "Erro ao carregar receita", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread { binding.progressBar.visibility = View.GONE }

                if (response.isSuccessful) {
                    val jsonObj = JSONObject(response.body?.string())

                    runOnUiThread {
                        binding.edtTitulo.setText(jsonObj.optString("titulo", ""))
                        binding.edtIngredientes.setText(jsonObj.optJSONArray("ingredientes")?.join("\n")?.replace("\"", "") ?: "")
                        binding.edtModoPreparo.setText(jsonObj.optJSONArray("modoPreparo")?.join("\n")?.replace("\"", "") ?: "")
                        binding.edtDicas.setText(jsonObj.optJSONArray("dicas")?.join("\n")?.replace("\"", "") ?: "")
                        binding.switchPrivada.isChecked = jsonObj.optBoolean("privado", false)
                    }
                }
            }
        })
    }

    private fun atualizarReceita() {
        val titulo = binding.edtTitulo.text.toString().trim()
        val ingredientes = binding.edtIngredientes.text.toString().split("\n").filter { it.isNotBlank() }
        val modoPreparo = binding.edtModoPreparo.text.toString().split("\n").filter { it.isNotBlank() }
        val dicas = binding.edtDicas.text.toString().split("\n").filter { it.isNotBlank() }
        val privada = binding.switchPrivada.isChecked

        if (titulo.isEmpty() || ingredientes.isEmpty() || modoPreparo.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        val json = JSONObject()
        json.put("titulo", titulo)
        json.put("ingredientes", JSONArray(ingredientes))
        json.put("modoPreparo", JSONArray(modoPreparo))
        json.put("dicas", JSONArray(dicas))
        json.put("privado", privada)

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
        val url = "${ApiConfig.BASE_URL}/receitas/$receitaId"

        val request = Request.Builder().url(url).put(body).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditarReceitaActivity, "Erro ao salvar receita", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarReceitaActivity, "Receita atualizada!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarReceitaActivity, "Erro ao atualizar: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
