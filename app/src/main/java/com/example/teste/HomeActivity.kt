package com.example.diariodereceitas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val recipes = listOf(
            Recipe("Bolo de Limão", R.drawable.bolo_limao),
            Recipe("Bolo de Laranja", R.drawable.bolo_laranja),
            Recipe("Brigadeiro Trufado", R.drawable.brigadeiro),
            Recipe("Lasanha", R.drawable.lasanha)
        )

        recipeAdapter = RecipeAdapter(recipes)
        recyclerView.adapter = recipeAdapter
    }
}
