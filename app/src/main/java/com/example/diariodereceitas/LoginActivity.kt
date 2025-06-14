package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diariodereceitas.databinding.ActivityLoginBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var senhaVisivel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar ou ocultar senha
        binding.mostrarSenhaImage.setOnClickListener {
            senhaVisivel = !senhaVisivel
            if (senhaVisivel) {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.passwordEditText.setSelection(binding.passwordEditText.text?.length ?: 0)
        }

        // Ação do botão de login
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val senha = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            autenticarUsuario(email, senha)
        }

        // Esqueceu a senha
        binding.esqueceuSenhaText.setOnClickListener {
            Toast.makeText(this, "Em breve enviaremos um link de recuperação.", Toast.LENGTH_SHORT).show()
        }

        // ABRIR TELA DE CADASTRO
        binding.criarContaText.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    // Função fora do onCreate, mas dentro da classe!
    private fun autenticarUsuario(email: String, senha: String) {
        val url = "${ApiConfig.BASE_URL}/auth/login" // Troque para o endpoint correto do seu backend

        val json = JSONObject()
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

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respStr = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login realizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Exemplo: abrir a Home
                        // startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        // finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login inválido ou erro do servidor!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
