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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = Firebase.auth

        // Init UI component
        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        name = findViewById(R.id.nameEditText)
        confirmPassword = findViewById(R.id.confirmpasswordEditText)
        signupButton = findViewById(R.id.signupButton)
        val loginTextView: TextView = findViewById(R.id.loginTextView)

        // LoginTextView -> LoginActivity
        loginTextView.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            if (name.text.toString().trim() == "") {
                Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            } else if (email.text.toString().trim() == "") {
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            } else if (password.text.toString().trim() == "") {
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            } else if (confirmPassword.text.toString().trim() == "") {
                Toast.makeText(this, " Confirm password is empty", Toast.LENGTH_SHORT).show()
            }

            if (password.text.toString().trim() == confirmPassword.text.toString().trim()) {
                Toast.makeText(this,"SIGN UP DONE",Toast.LENGTH_SHORT).show()
                auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString())
                    .addOnSuccessListener(this) { task ->
                        Log.d("FirebaseAuth","Create success")
                        val user = auth.currentUser
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            } else {
                Toast.makeText(this,"PASS NOT MATCH. SIGN AGAIN",Toast.LENGTH_SHORT).show()
                password.setText("")
                confirmPassword.setText("")
            }

        }


    }
}