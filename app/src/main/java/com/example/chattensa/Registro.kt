package com.example.chattensa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattensa.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etPassword
import kotlinx.android.synthetic.main.activity_login.etUsuario
import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*

class Registro : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)


        btnRegistro.setOnClickListener {
            performRegister()
        }

            tvLogin.setOnClickListener {
                Log.d("Registro", "Intenta mostrar el Login Activity")

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

            btnImg.setOnClickListener {
                Log.d("Registro", "Elige foto")

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }

        var selectedPhotoUri: Uri? = null

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
                Log.d("Registro", "Foto seleccionada")

                selectedPhotoUri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

                imageView.setImageBitmap(bitmap)
                btnImg.alpha=0f

               /* val bitmapDrawable = BitmapDrawable(bitmap)
                btnImg.setBackgroundDrawable(bitmapDrawable)*/
            }

        }

    private fun performRegister() {
        val usuario = etUsuario.text.toString()
        val password = etPassword.text.toString()

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce los campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }


        Log.d("Registro", "Usuario es: " + usuario)
        Log.d("Registro", "Password: $password")


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if succesful
                Log.d("Registro", "Bien creado usuario con uid: ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("Registro", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Imagen bien cargada: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {


                    Log.d("Registro","File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }

            }
            .addOnFailureListener{
                Log.d("Registro", "No se cargó la imagen: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String) {
        val uid=FirebaseAuth.getInstance().uid ?:""
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user= User(uid,etUsuarioReg.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Registro","Guardamos el usuario en la BBDD")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("Registro", "Fallo al enviar los datos a la BBDD: ${it.message}")
            }

    }
}
