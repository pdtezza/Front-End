package com.example.diariodereceitas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diariodereceitas.databinding.ItemReceitaBinding

class MeuPerfilAdapter(
    private val context: Context,
    private val receitasPublicas: List<Receita>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_PRIVADAS = 0
    private val VIEW_TYPE_RECEITA = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_PRIVADAS else VIEW_TYPE_RECEITA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PRIVADAS) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_pasta_privada, parent, false)
            PastaPrivadaViewHolder(view)
        } else {
            val binding = ItemReceitaBinding.inflate(LayoutInflater.from(context), parent, false)
            ReceitaViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = receitasPublicas.size + 1 // +1 por causa da pasta privadas

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PastaPrivadaViewHolder) {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MinhasPrivadasActivity::class.java)
                context.startActivity(intent)
            }
        } else if (holder is ReceitaViewHolder) {
            val receita = receitasPublicas[position - 1]  // -1 por causa da pasta na posição 0

            holder.binding.txtNomeReceita.text = receita.nome

            Glide.with(context)
                .load(receita.imagemUrl)
                .placeholder(R.drawable.bololimao)
                .into(holder.binding.imgReceita)

            // 🔴 Oculta o botão de like no MeuPerfilActivity
            holder.binding.btnLike.visibility = View.GONE

            holder.binding.root.setOnClickListener {
                val intent = Intent(context, EditarReceitaActivity::class.java)
                intent.putExtra("receitaId", receita.id)
                context.startActivity(intent)
            }
        }
    }

    inner class PastaPrivadaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tituloPasta)
    }

    inner class ReceitaViewHolder(val binding: ItemReceitaBinding) : RecyclerView.ViewHolder(binding.root)
}
