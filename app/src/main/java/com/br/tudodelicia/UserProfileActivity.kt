package com.br.tudodelicia

import android.content.Intent
import android.graphics.Bitmap
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
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var profileImage: ImageView
    private val db = com.google.firebase.Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        enableEdgeToEdge()
        auth = Firebase.auth

        profileImage = findViewById<ImageView>(R.id.profileImage)
        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val iconEdit = findViewById<ImageButton>(R.id.iv_recipe_image_button)

        profileImage.setOnClickListener({ openImagePicker() })
        iconEdit.setOnClickListener({ openImagePicker() })

        val user = auth.currentUser

        if (user != null) {
            profileName.text = user.displayName ?: "Nome não disponível"
            profileEmail.text = user.email ?: "Email não disponível"
        }

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        loadProfilePhoto(user!!.uid.toString())
    }

    private fun loadProfilePhoto(uid: String) {

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

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uriToFile( uri: Uri): File? {
        val contentResolver = this.contentResolver
        val tempFile = File.createTempFile("temp_image", ".jpg", this.cacheDir) // Cria um arquivo temporário
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun imageFileToBase64(filePath: Uri): String? {
        val file = uriToFile(filePath)
        if (!file?.exists()!!) return null
        val bitmap = BitmapFactory.decodeFile(file.path)
        return bitmapToBase64(bitmap)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImage.setImageURI(selectedImageUri)
            if(selectedImageUri != null) {
                val imgBase64 = imageFileToBase64(selectedImageUri!!)
                val userImage = mapOf(
                    "uid" to auth.currentUser?.uid,
                    "img" to imgBase64,
                )
                val db = com.google.firebase.Firebase.firestore
                db.collection("usersImages").add(userImage).addOnSuccessListener {
                    Toast.makeText(this, "Imagem alterada com sucesso!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Falha ao alterar a imagem!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}