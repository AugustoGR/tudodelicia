package com.br.tudodelicia

import android.os.Bundle // Para o método onCreate
import androidx.appcompat.app.AppCompatActivity // Base para atividades
import androidx.recyclerview.widget.LinearLayoutManager // Gerenciador de layout para RecyclerView
import androidx.recyclerview.widget.RecyclerView // Widget de lista
import androidx.appcompat.widget.SearchView // Barra de pesquisa
import android.widget.Toast // Para exibir mensagens de depuração ou feedback ao usuário


class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: YourAdapter // Substitua pelo seu adaptador
    private val itemList = mutableListOf<String>() // Lista de itens para exibição

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.search_results_recycler)

        // Configurar RecyclerView
        adapter = YourAdapter(itemList)
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

    private fun performSearch(query: String) {
        // Atualize os dados no adaptador com base no termo de busca
        val filteredList = itemList.filter { it.contains(query, ignoreCase = true) }
        adapter.updateList(filteredList)
    }
}
