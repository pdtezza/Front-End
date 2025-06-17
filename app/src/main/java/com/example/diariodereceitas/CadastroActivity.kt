package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diariodereceitas.databinding.ActivityCadastroBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cadastrarButton.setOnClickListener {
            val nome = binding.nomeEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val senha = binding.senhaEditText.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarUsuario(nome, email, senha)
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        val url = "${ApiConfig.BASE_URL}/clientes/cadastrar"
        val json = JSONObject()
        json.put("nome", nome)
        json.put("email", email)
        json.put("senha", senha)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CadastroActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)

                        val usuarioId = jsonResponse.optString("id", "")
                        val usuarioNome = jsonResponse.optString("nome", "")

                        if (usuarioId.isNotEmpty()) {
                            val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            sharedPref.edit()
                                .putString("usuarioId", usuarioId)
                                .putString("usuarioNome", usuarioNome)
                                .apply()
                        }

                        Toast.makeText(this@CadastroActivity, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@CadastroActivity, "Erro no cadastro: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
