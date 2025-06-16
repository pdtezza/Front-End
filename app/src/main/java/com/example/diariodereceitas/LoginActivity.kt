package com.example.diariodereceitas

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diariodereceitas.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var senhaVisivel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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

        // Botão de login (Firebase Auth)
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val senha = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                        // Vá para a home
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Usuário ou senha inválidos!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Esqueceu a senha (envia link pelo Firebase)
        binding.esqueceuSenhaText.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite o e-mail para recuperar a senha.", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "E-mail enviado!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao enviar e-mail.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Link para cadastro
        binding.criarContaText.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}
