package com.br.tudodelicia

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle // Para o método onCreate
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity // Base para atividades
import androidx.recyclerview.widget.LinearLayoutManager // Gerenciador de layout para RecyclerView
import androidx.recyclerview.widget.RecyclerView // Widget de lista
import androidx.appcompat.widget.SearchView // Barra de pesquisa
import android.widget.Toast // Para exibir mensagens de depuração ou feedback ao usuário
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth


class Search : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeCardAdapter: RecipeCardAdapter
    private val recipeList = mutableListOf<Recipe>()
    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_2

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.item_3 -> {
                    startActivity(Intent(this, Favorited::class.java))
                    true
                }
                else -> false
            }
        }

        auth = Firebase.auth

        searchView = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.search_results_recycler)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recipeCardAdapter = RecipeCardAdapter(
            foodList = recipeList,
            onItemClicked =  { documentId ->
                val intent = Intent(this, RecipeDetail::class.java)
                intent.putExtra("documentId", documentId) // Passa o documentId
                startActivity(intent)
            },
            currentUserId = currentUserId!!
        )
        recyclerView.adapter = recipeCardAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

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
                    val id = document.id
                    val likedByUsers: MutableList<String> = document.get("likedByUsers") as? MutableList<String> ?: mutableListOf()
                    val likeCount = document.get("likeCount") as? Number ?: 0
                    val commentsCount = document.get("commentsCount") as? Number ?: 0
                    recipeList.add(Recipe(title,description,decodedBitmap,id,likedByUsers,likeCount.toInt(),commentsCount.toInt()))
                }
                recipeCardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w( "Error adding document", e)
            }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.item_2
    }

    private fun performSearch(query: String) {
        val filteredList = recipeList.filter { recipe ->
            recipe.title?.contains(query, ignoreCase = true) == true
        }
        recipeCardAdapter.updateList(filteredList)
    }
}
