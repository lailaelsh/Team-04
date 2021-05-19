package com.example.traveltogether

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_conversation_fragment.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class conversation_fragment : Fragment() {

    private val args: conversation_fragmentArgs by navArgs()
    lateinit var conversationRecyclerView: RecyclerView
    var messages:  MutableList<Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conversation_fragment, container, false)
        conversationRecyclerView = view.findViewById(R.id.conversationRecyclerView)
        val activity = activity as Context
        val helperAdapter: ConversationHelperAdapter = ConversationHelperAdapter(activity, messages, this)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(activity)
        conversationRecyclerView .layoutManager = linearLayoutManager
        conversationRecyclerView.adapter = helperAdapter
        val firebaseref = FirebaseDatabase.getInstance().reference
        firebaseref.child("posts").child(args.chatId).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for ( msg in snapshot.children) {
                    val name = msg.child("name").value.toString()
                    val uid = msg.child("uid").value.toString()
                    val time = msg.child("time").value as Long
                    val text = msg.child("message").value.toString()
                    val msgItem = Message(text,uid,name,time)
                    messages.add(msgItem)
                }
                helperAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        sendMessageButton.setOnClickListener{
            val up  =  firebaseref.child("posts").child(args.chatId) as UserPost
            up.addMessage(messageEditText.text.toString())
        }
        return view
    }

}