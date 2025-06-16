package com.example.diariodereceitas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diariodereceitas.databinding.AddReceitaActivityBinding
import com.google.firebase.storage.FirebaseStorage
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AddReceitaActivity : AppCompatActivity() {

    private lateinit var binding: AddReceitaActivityBinding
    private var fotoUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddReceitaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltar.setOnClickListener { finish() }

        binding.btnFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnAnexo.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de anexo ainda não implementada.", Toast.LENGTH_SHORT).show()
        }




        binding.btnSalvar.setOnClickListener {
            val titulo = binding.edtTitulo.text.toString().trim()
            val ingredientesText = binding.edtIngredientes.text.toString().trim()
            val modoPreparoText = binding.edtModoPreparo.text.toString().trim()
            val dicasText = binding.edtDicas.text.toString().trim()
            val privada = binding.switchPrivada.isChecked

            if (titulo.isEmpty() || ingredientesText.isEmpty() || modoPreparoText.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ingredientes = ingredientesText.split("\n").filter { it.isNotBlank() }
            val modoPreparo = modoPreparoText.split("\n").filter { it.isNotBlank() }
            val dicas = dicasText.split("\n").filter { it.isNotBlank() }

            val usuarioId = "user123" // Substitua por id do usuário logado
            val usuarioNome = "Pedro" // Substitua por nome do usuário logado

            if (fotoUri != null) {
                uploadFotoEEnviarReceita(
                    titulo, ingredientes, modoPreparo, dicas, privada,
                    usuarioId, usuarioNome, fotoUri!!
                )
            } else {
                salvarReceitaComUrl(
                    titulo, ingredientes, modoPreparo, dicas, privada,
                    usuarioId, usuarioNome, ""
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            fotoUri = data?.data
            if (fotoUri != null) {
                binding.imgPreview.setImageURI(fotoUri)
                binding.imgPreview.visibility = View.VISIBLE
                Toast.makeText(this, "Foto selecionada!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFotoEEnviarReceita(
        titulo: String,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        dicas: List<String>,
        privada: Boolean,
        usuarioId: String,
        usuarioNome: String,
        fotoUri: Uri
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("receitas/${System.currentTimeMillis()}_foto.jpg")
        fileRef.putFile(fotoUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val fotoUrl = uri.toString()
                    salvarReceitaComUrl(
                        titulo, ingredientes, modoPreparo, dicas, privada,
                        usuarioId, usuarioNome, fotoUrl
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao subir foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarReceitaComUrl(
        titulo: String,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        dicas: List<String>,
        privada: Boolean,
        usuarioId: String,
        usuarioNome: String,
        fotoUrl: String
    ) {
        Toast.makeText(this, "Enviando receita...", Toast.LENGTH_SHORT).show()
        val url = "${ApiConfig.BASE_URL}/receitas"

        val json = JSONObject()
        json.put("titulo", titulo)
        json.put("ingredientes", JSONArray(ingredientes))
        json.put("modoPreparo", JSONArray(modoPreparo))
        json.put("dicas", JSONArray(dicas))
        json.put("usuarioId", usuarioId)
        json.put("usuarioNome", usuarioNome)
        json.put("fotoUrl", fotoUrl)
        json.put("privado", privada)
        json.put("autorId", usuarioId)
        json.put("likes", 0)
        json.put("visualizacoes", 0)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddReceitaActivity,
                        "Erro ao salvar receita: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddReceitaActivity,
                            "Receita salva com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddReceitaActivity,
                            "Erro ao salvar receita: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
