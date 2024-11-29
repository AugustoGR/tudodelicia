package com.br.tudodelicia

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RecipeDetail : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextComment: EditText
    private lateinit var buttonSendComment: Button

    private val comments = mutableListOf<Comment>()
    private lateinit var commentsAdapter: CommentsAdapter

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val documentId = intent.getStringExtra("documentId")
        if (documentId != null) {
            fetchRecipeDetails(documentId)
        }

        recyclerView = findViewById(R.id.recyclerViewComments)
        editTextComment = findViewById(R.id.editTextComment)
        buttonSendComment = findViewById(R.id.buttonSendComment)

        commentsAdapter = CommentsAdapter(comments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentsAdapter

        loadComments()

        buttonSendComment.setOnClickListener {
            val text = editTextComment.text.toString().trim()
            if (text.isNotEmpty()) {
                postComment(text)
            }
        }
    }

    private fun fetchRecipeDetails(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("recipes").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title")
                    val description = document.getString("description")
                    val ingredients = document.get("ingredients") as? List<String>
                    val steps = document.get("steps") as? List<String>
                    val imgBase64 = document.getString("img")

                    findViewById<TextView>(R.id.titleTextView).text = title

                    if (imgBase64 != null) {
                        val decodedBytes = Base64.decode(imgBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        findViewById<ImageView>(R.id.recipeImageView).setImageBitmap(bitmap)
                    }

                    val ingredientsLayout = findViewById<LinearLayout>(R.id.ingredientsLayout)
                    ingredients?.forEach { ingredient ->
                        val ingredientTextView = TextView(this).apply {
                            text = "• $ingredient"
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f)
                            typeface = ResourcesCompat.getFont(context, R.font.poppins)
                        }
                        ingredientsLayout.addView(ingredientTextView)
                    }

                    val stepsLayout = findViewById<LinearLayout>(R.id.stepsLayout)
                    steps?.forEachIndexed { index, step ->
                        val stepTitleTextView = TextView(this).apply {
                            text = "Passo ${index + 1}:"
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
                            typeface = ResourcesCompat.getFont(context, R.font.poppins_bold)
                        }
                        stepsLayout.addView(stepTitleTextView)

                        val stepDescriptionTextView = TextView(this).apply {
                            text = step
                            setTextColor(ContextCompat.getColor(context, R.color.gray))
                            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
                            typeface = ResourcesCompat.getFont(context, R.font.poppins)
                        }
                        stepsLayout.addView(stepDescriptionTextView)
                    }
                } else {
                    Log.e("RecipeDetailActivity", "Documento não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("RecipeDetailActivity", "Erro ao buscar os dados: ", e)
            }
    }

    private fun goToList(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun loadComments() {
        db.collection("recipes")
            .document(intent.getStringExtra("documentId")!!)
            .collection("comments")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Erro ao carregar comentários: ${e.message}")
                    return@addSnapshotListener
                }

                comments.clear()
                snapshot?.documents?.forEach { document ->
                    val comment = document.toObject(Comment::class.java)
                    comment?.let { comments.add(it) }
                }
                commentsAdapter.notifyDataSetChanged()
            }
    }

    private fun postComment(text: String) {
        val user = auth.currentUser ?: return

        val comment = hashMapOf(
            "userName" to (user.displayName ?: "Fulano:"),
            "text" to text
        )

        val recipeRef = db.collection("recipes")
            .document(intent.getStringExtra("documentId")!!)

        recipeRef
            .collection("comments")
            .add(comment)

        recipeRef.update("commentsCount", FieldValue.increment(1))
    }

}