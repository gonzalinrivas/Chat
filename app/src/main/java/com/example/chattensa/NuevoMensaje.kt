package com.example.chattensa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chattensa.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_nuevo_mensaje.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NuevoMensaje : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_mensaje)

        supportActionBar?.title = "Selecciona contacto"

        fetchUsers()

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    //ACTUALIZAR USUARIOS BBDD
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("New Message", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))

                    }

                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                   // intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()

                }
                recylcerview_nuevoMensaje.adapter = adapter
            }


            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
//PARA VER LISTA USUARIOS UNA VEZ QUE HAYA BBDD ------
        viewHolder.itemView.user_name_tv_newMessage.text = user.username

        //CARGAR IMAGEN DEL USUARIO DE LA BBDD
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newMessage)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}



