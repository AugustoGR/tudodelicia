package com.br.tudodelicia

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle // Para o método onCreate
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity // Base para atividades
import androidx.recyclerview.widget.LinearLayoutManager // Gerenciador de layout para RecyclerView
import androidx.recyclerview.widget.RecyclerView // Widget de lista
import androidx.appcompat.widget.SearchView // Barra de pesquisa
import android.widget.Toast // Para exibir mensagens de depuração ou feedback ao usuário
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth


class Search : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeCardAdapter
    private val itemList = mutableListOf<Recipe>() // Lista de itens para exibição
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        auth = Firebase.auth

        searchView = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.search_results_recycler)

        // Configurar RecyclerView
        adapter = RecipeCardAdapter(itemList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Configurar busca
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
        val recipeList = mutableListOf<Recipe>()
        val recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler)
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

    private fun performSearch(query: String) {
        // Filtrar os itens com base no título
        val filteredList = itemList.filter { recipe ->
            recipe.title?.contains(query, ignoreCase = true) == true
        }
        // Atualizar o adaptador com a lista filtrada
        adapter.updateList(filteredList)
    }
}
