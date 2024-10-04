package com.br.tudodelicia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun goToLogon(view: View) {
        val intent = Intent(this, Logon::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        val userPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val name = userPreferences.getString("name", null)
        val password = userPreferences.getString("password", null)

        val passwordInput = findViewById<EditText>(R.id.passwordInput).text.toString()
        val userInput = findViewById<EditText>(R.id.userInput).text.toString()
        Log.d("LoginDebug",name+"")
        Log.d("LoginDebug",password+"")
        if ((name == userInput) && (password == passwordInput)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val toast = Toast.makeText(this, "Falha no login", Toast.LENGTH_SHORT) // in Activity
            toast.show()
        }

    }
}