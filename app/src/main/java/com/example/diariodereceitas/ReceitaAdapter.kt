package com.example.diariodereceitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diariodereceitas.databinding.ItemReceitaBinding

class ReceitaAdapter(
    private val receitas: List<Receita>
) : RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder>() {

    inner class ReceitaViewHolder(val binding: ItemReceitaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val binding = ItemReceitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = receitas[position]
        holder.binding.txtNomeReceita.text = receita.nome
        holder.binding.txtAutor.text = "por ${receita.autor}"
        holder.binding.txtLikes.text = "${receita.likes} likes"
        holder.binding.txtVisualizacoes.text = "${receita.visualizacoes} views"
        // Se for usar Glide para imagem (recomendado):
        // Glide.with(holder.itemView).load(receita.imagemUrl).into(holder.binding.imgReceita)
    }

    override fun getItemCount() = receitas.size
}
