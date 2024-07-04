package com.example.todo_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spash_screen)

        supportActionBar?.hide()
        // Chua check user auth
        val intent = Intent(this, MainActivity::class.java)
        //Delay
        Handler(Looper.getMainLooper()).postDelayed( {
            startActivity(intent)
            finish()
        },2000)

    }
}