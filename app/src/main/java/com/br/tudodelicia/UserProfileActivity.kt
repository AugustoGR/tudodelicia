package com.br.tudodelicia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.util.Base64

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = Firebase.auth

        // Referências para os elementos da tela
        val profileImage = findViewById<ImageView>(R.id.profileImage)
                val profileName = findViewById<TextView>(R.id.profileName)
                val profileEmail = findViewById<TextView>(R.id.profileEmail)
                val profileDob = findViewById<TextView>(R.id.profileDob)
                val logoutButton = findViewById<Button>(R.id.logoutButton)

                // Obter o usuário atual
                val user = auth.currentUser

        if (user != null) {
            // Exibir dados do usuário
            profileName.text = user.displayName ?: "Nome não disponível"
            profileEmail.text = user.email ?: "Email não disponível"
            // Carregar a foto de perfil, se disponível
            if (user.photoUrl != null) {
                // Carregar imagem de perfil, por exemplo, com uma biblioteca como Glide
                // Glide.with(this).load(user.photoUrl).into(profileImage)
            } else {
                // Defina uma imagem de placeholder ou padrão
                profileImage.setImageResource(R.drawable.ic_profile_placeholder)
            }

            // Exibir data de nascimento se disponível (caso tenha sido salva no Firestore ou outro banco de dados)
            val db = Firebase.firestore
            db.collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                    val dob = document.getString("dob") // Supondo que a data de nascimento esteja salva em Firestore
                profileDob.text = dob ?: "Data de nascimento não disponível"
            }
        }

        // Implementar o logout
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, Login::class.java)  // Tela de login após deslogar
            startActivity(intent)
            finish()  // Finaliza a atividade atual
        }
    }
}