package com.zachthelen.cbb_predict

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class GameCommentActivity : AppCompatActivity() {
    private lateinit var gameId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_comment)

        gameId = intent.getStringExtra("GAME_ID") ?: return

        recyclerView = findViewById(R.id.commentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CommentAdapter()
        recyclerView.adapter = adapter

        commentEditText = findViewById(R.id.commentEditText)
        sendButton = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                commentEditText.text.clear()
            }
        }

        // Fetch comments for this game
        fetchComments()
    }

    private fun addComment(commentText: String) {
        val comment = Comment(userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "",
            text = commentText)
        FirebaseFirestore.getInstance().collection("games")
            .document(gameId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener { documentReference ->
                adapter.addComment(comment)
            }
    }

    private fun fetchComments() {
        FirebaseFirestore.getInstance().collection("games")
            .document(gameId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("GameCommentActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots?.documentChanges ?: emptyList()) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> adapter.addComment(dc.document.toObject(Comment::class.java))
                        // Handle other changes if needed
                        DocumentChange.Type.MODIFIED -> TODO()
                        DocumentChange.Type.REMOVED -> TODO()
                    }
                }
            }
    }
}