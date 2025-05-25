package com.example.teste

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecipeAdapter(private val recipes: List<recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeTitle.text = recipe.name
        holder.recipeImage.setImageResource(recipe.imageResId)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (recipe.name == "Privado") {
                val intent = Intent(context, ReceitaPrivadaActivity::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ReceitaActivity::class.java)
                intent.putExtra("receitaNome", recipe.name)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = recipes.size
}
