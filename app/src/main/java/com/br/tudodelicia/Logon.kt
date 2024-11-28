package com.br.tudodelicia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class Logon : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.log_on)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.log_on)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        var logonButton = findViewById<Button>(R.id.logonButton)
        logonButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                logon(view)
            }
        })
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun logon(view: View) {
        val password = findViewById<EditText>(R.id.passwordInput).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.passwordConfInput).text.toString()
        val email = findViewById<EditText>(R.id.emailInput).text.toString()
        val userName = findViewById<EditText>(R.id.userInput).text.toString()

        if (password != confirmPassword ) {
            Toast.makeText(
                baseContext,
                "A senha não condiz com a confirmação.",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
//                    val profileUpdates = userProfileChangeRequest {
//                        displayName = userName
//                    }
//                    user!!.updateProfile(profileUpdates)
                    Toast.makeText(
                        baseContext,
                        "Conta cadastrada com sucesso.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}
