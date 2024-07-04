package com.example.todo_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init UI
        email = findViewById(R.id.login_emailEditText)
        password = findViewById(R.id.login_passwordEditText)

        auth = Firebase.auth

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            // Handle login logic here
            if(email.text.toString().trim() == "") {
                Toast.makeText(this,"Email is empty",Toast.LENGTH_SHORT).show()
            } else if (password.text.toString().trim() == "") {
                Toast.makeText(this,"Password is empty",Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)
                            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(baseContext,"Authentication failed.", Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }


            }


        }

        val forgotPasswordTextView: TextView = findViewById(R.id.forgotPasswordTextView)
        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password logic here
        }

        val signupTextView: TextView = findViewById(R.id.signupTextView)
        signupTextView.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }



    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
