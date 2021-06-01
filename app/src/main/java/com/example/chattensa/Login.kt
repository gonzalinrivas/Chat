package com.example.chattensa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        btnLogin.setOnClickListener {
            performLogin()
           /* val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)*/
        }
        tvBackRegister.setOnClickListener {
            finish()
        }


    }

    private fun performLogin() {
        val usuario = etUsuario.text.toString()
        val password = etPassword.text.toString()


        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa con datos", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener


                ///POSIBLE ERROR---   !!      ---------------------------------------------------------------------
                Log.d("Login", "Bien log in: ${it.result!!.user!!.uid}")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Fallo en el log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}


