package com.example.chattensa

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chattensa.models.ChatMessage
import com.example.chattensa.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerView_chatLog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NuevoMensaje.USER_KEY)


        //CAMBIAR TITULO BARRA MENU SUPERIOR
        supportActionBar?.title = toUser?.username


        listenForMessages()

        sendButton.setOnClickListener {
            Log.d(TAG, "Attemp to send message...")
            performSendMessage()

        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessageActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerView_chatLog.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    //Mandar mensaje a la BBDD


    private fun performSendMessage() {

        val text = et_chatLog.text.toString()

        val user = intent.getParcelableExtra<User>(NuevoMensaje.USER_KEY)
        val fromId = FirebaseAuth.getInstance().uid

        //POSIBLE ERRROR----------------
        val toId = user!!.uid


        if (fromId == null) return

        val reference =
            FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()

        val toReference =
            FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(
            reference.key!!, text, fromId, toId, System
                .currentTimeMillis() / 1000
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Guardar nuestro chat:${reference.key}")
                et_chatLog.text.clear()
                recyclerView_chatLog.scrollToPosition(adapter.itemCount - 1)

            }
        toReference.setValue(chatMessage)


        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }


}




