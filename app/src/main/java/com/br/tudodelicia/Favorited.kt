package com.br.tudodelicia

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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


class Favorited : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.favorited_recipes)

        profileImage = findViewById(R.id.profileImage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_3

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_2 -> {
                    startActivity(Intent(this, Search::class.java))
                    true
                }
                R.id.item_1 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        val recipeList = mutableListOf<Recipe>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recipeCardAdapter = RecipeCardAdapter(
            foodList = recipeList,
            onItemClicked =  { documentId ->
                val intent = Intent(this, RecipeDetail::class.java)
                intent.putExtra("documentId", documentId)
                startActivity(intent)
            },
            currentUserId = currentUser?.uid!!
        )
        recyclerView.adapter = recipeCardAdapter
        val db = Firebase.firestore
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result ) {
                    val likedByUsers: MutableList<String> = document.get("likedByUsers") as? MutableList<String> ?: mutableListOf()
                    if (likedByUsers.contains(currentUser.uid)) {
                        val title: String? = document.getString("title")
                        val description: String? = document.getString("description")
                        val decodedBytes: ByteArray = Base64.decode(document.getString("img"), Base64.DEFAULT)
                        val decodedBitmap =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        val id = document.id
                        val likeCount = document.get("likeCount") as? Number ?: 0
                        val commentsCount = document.get("commentsCount") as? Number ?: 0
                        recipeList.add(Recipe(title,description,decodedBitmap,id,likedByUsers,likeCount.toInt(),commentsCount.toInt()))
                    }
                }
                recipeCardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w( "Error adding document", e)
            }

        findViewById<TextView>(R.id.user_name).text = currentUser.displayName
        loadProfilePhoto(currentUser.uid)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.item_3
    }

    fun goToProfile(view: View) {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

    fun goToCreateRecipe(view: View) {
        val intent = Intent(this, CreateRecipe::class.java)
        startActivity(intent)
    }

    private fun loadProfilePhoto(uid: String) {
        val db = Firebase.firestore
        db.collection("usersImages").whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // Pega o primeiro resultado
                    val base64Image = document.getString("img") ?: ""
                    val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                    profileImage.setImageBitmap(bitmap)
                } else {
                    println("Nenhum documento encontrado para o UID do usuário.")
                    profileImage.setImageResource(R.drawable.augusto)
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar imagem do Firestore: ${e.message}")
                profileImage.setImageResource(R.drawable.augusto)
            }
    }

}