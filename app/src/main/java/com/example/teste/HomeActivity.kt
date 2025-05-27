package com.example.diariodereceitas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teste.R
import com.example.teste.Recipe


class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: com.example.teste.RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val recipes = listOf(
            Recipe("Bolo de Limão", R.drawable.bololimao),
            Recipe("Bolo de Laranja", R.drawable.bololaranja),
            Recipe("Brigadeiro Trufado", R.drawable.brigadeirotrufado),
            Recipe("Lasanha", R.drawable.lasanha),
            Recipe("Privado", R.drawable.cadeado)
        )

        recipeAdapter = com.example.teste.RecipeAdapter(recipes)
        recyclerView.adapter = recipeAdapter
    }
}
