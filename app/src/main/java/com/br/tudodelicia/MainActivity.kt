package com.br.tudodelicia

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    // Ação para o item "Início"
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.item_2 -> {
                    // Ação para o item "Pesquisa"
                    startActivity(Intent(this, Search::class.java))
                    true
                }
                /*R.id.item_3 -> {
                    // Ação para o item "Favoritos"
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }*/
                else -> false
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        val recipeList = mutableListOf<Recipe>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recipeCardAdapter = RecipeCardAdapter(recipeList)
        recyclerView.adapter = recipeCardAdapter
        val db = Firebase.firestore
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result ) {
                    val title: String? = document.getString("title")
                    val description: String? = document.getString("description")
                    val decodedBytes: ByteArray = Base64.decode(document.getString("img"), Base64.DEFAULT)
                    val decodedBitmap =
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    recipeList.add(Recipe(title,description,decodedBitmap))
                }
                recipeCardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w( "Error adding document", e)
            }

    }
    fun logout(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    fun goToRecipe(view: View) {
        val intent = Intent(this, RecipeDetail::class.java)
        startActivity(intent)
    }

    fun goToCreateRecipe(view: View) {
        val intent = Intent(this, CreateRecipe::class.java)
        startActivity(intent)
    }

}