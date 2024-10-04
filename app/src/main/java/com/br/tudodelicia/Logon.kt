package com.br.tudodelicia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Logon : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.log_on)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.log_on)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun logon(view: View) {
        val userPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)

        val passwordInput = findViewById<EditText>(R.id.passwordInput).text.toString()
        val userInput = findViewById<EditText>(R.id.userInput).text.toString()
        println(passwordInput)
        println(userInput)
        userPreferences.edit().putString("name", userInput).apply()
        userPreferences.edit().putString("password", passwordInput).apply()

        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
