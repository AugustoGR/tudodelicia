package com.br.tudodelicia

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)

        if (!userPreferences.contains("name")) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        val recipeList = listOf(
            Recipe("Hamburger", "Hamburger bem delicioso hmm venha experimentar", R.drawable.hamburguinho),
            Recipe("Massa Carbonara?", "massa bem gostosa que talvez seja carbonara não tenho certeza", R.drawable.massa),
            Recipe("Salada do César", "alface e frango basicamente", R.drawable.cesar),
            Recipe("Hamburger Vegano", "Pão, alface, queijo vegetal com molho e carne de soja", R.drawable.hamburguinho)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val recipeCardAdapter = RecipeCardAdapter(recipeList)
        recyclerView.adapter = recipeCardAdapter

    }
    fun logout(view: View) {
        deleteSharedPreferences("userPreferences")
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}