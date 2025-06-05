package com.example.diariodereceitas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var senhaVisivel = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val esqueceuSenhaText = findViewById<TextView>(R.id.esqueceuSenhaText)
        val mostrarSenha = findViewById<ImageView>(R.id.mostrarSenhaImage)

        // Mostrar ou ocultar senha ao clicar no ícone
        mostrarSenha.setOnClickListener {
            senhaVisivel = !senhaVisivel
            if (senhaVisivel) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text.length) // mantém o cursor no final
        }

        // Ação do botão "Entrar"
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Use apenas contas @gmail.com", Toast.LENGTH_SHORT).show()
            } else {
                // Simula login com sucesso
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Ação do texto "Esqueceu a senha?"
        esqueceuSenhaText.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Recuperação de senha")
                .setMessage("Um link de recuperação foi enviado para seu email.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
