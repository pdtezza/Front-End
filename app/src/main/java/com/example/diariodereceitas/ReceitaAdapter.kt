package com.example.diariodereceitas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diariodereceitas.databinding.ItemReceitaBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.io.IOException

class ReceitaAdapter(
    private val receitas: MutableList<Receita>,
    var favoritosSet: Set<String>
) : RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder>() {

    inner class ReceitaViewHolder(val binding: ItemReceitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val binding = ItemReceitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = receitas[position]
        holder.binding.txtNomeReceita.text = receita.nome

        Glide.with(holder.itemView)
            .load(receita.imagemUrl)
            .placeholder(R.drawable.bololimao)
            .into(holder.binding.imgReceita)

        val curtiu = favoritosSet.contains(receita.id)
        if (curtiu) {
            holder.binding.btnLike.setColorFilter(0xFFFF7B1D.toInt()) // Laranja
        } else {
            holder.binding.btnLike.setColorFilter(0xFFBBBBBB.toInt()) // Cinza claro
        }

        holder.binding.btnLike.setOnClickListener {
            if (curtiu) {
                removerLike(holder, receita, position)
            } else {
                enviarLike(holder, receita, position)
            }
        }

        // Clique no card inteiro para abrir detalhes
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ReceitaDetalheActivity::class.java)
            intent.putExtra("fotoUrl", receita.imagemUrl)
            intent.putExtra("titulo", receita.nome)
            intent.putExtra("autor", receita.autor)
            intent.putStringArrayListExtra(
                "ingredientes",
                ArrayList(receita.ingredientes.split("\n").map { it.trim() }
                    .filter { it.isNotBlank() })
            )
            intent.putStringArrayListExtra(
                "modoPreparo",
                ArrayList(receita.modoPreparo.split("\n").map { it.trim() }
                    .filter { it.isNotBlank() })
            )
            intent.putStringArrayListExtra(
                "dicas",
                ArrayList(receita.dicas.split("\n").map { it.trim() }.filter { it.isNotBlank() })
            )
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = receitas.size

    private fun enviarLike(holder: ReceitaViewHolder, receita: Receita, position: Int) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) {
            holder.itemView.post {
                Toast.makeText(holder.itemView.context, "Faça login para curtir!", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val url = "${ApiConfig.BASE_URL}/receitas/${receita.id}/like?clienteId=$usuarioId"
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0)))
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                holder.itemView.post {
                    Toast.makeText(holder.itemView.context, "Erro ao curtir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                holder.itemView.post {
                    if (response.isSuccessful) {
                        // Adiciona localmente no set para atualizar UI
                        favoritosSet = favoritosSet + receita.id
                        notifyItemChanged(position)
                        Toast.makeText(holder.itemView.context, "Você curtiu!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Erro ao curtir: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun removerLike(holder: ReceitaViewHolder, receita: Receita, position: Int) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) {
            holder.itemView.post {
                Toast.makeText(holder.itemView.context, "Faça login para remover o like!", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val url = "${ApiConfig.BASE_URL}/receitas/${receita.id}/like?clienteId=$usuarioId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                holder.itemView.post {
                    Toast.makeText(holder.itemView.context, "Erro ao remover like: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                holder.itemView.post {
                    if (response.isSuccessful) {
                        // Remove localmente no set para atualizar UI
                        favoritosSet = favoritosSet - receita.id
                        notifyItemChanged(position)
                        Toast.makeText(holder.itemView.context, "Like removido!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Erro ao remover like: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}