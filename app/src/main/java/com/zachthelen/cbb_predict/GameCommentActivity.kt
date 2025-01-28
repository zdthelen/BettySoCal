package com.zachthelen.cbb_predict

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GameCommentActivity : AppCompatActivity() {
    private lateinit var gameId: String
    private lateinit var gameDetails: Matchup
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_comment)

        // Retrieve both gameId and gameDetails from Intent
        gameId = intent.getStringExtra("GAME_ID") ?: return
        gameDetails = intent.getParcelableExtra<Matchup>("GAME_DETAILS") ?: return

        // Use gameDetails to set up the UI with specific game information
        setupGameDetailsUI()

        recyclerView = findViewById(R.id.commentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with an empty list and gameId
        adapter = CommentAdapter(mutableListOf(), gameId)
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

    private fun setupGameDetailsUI() {
        // Game Title with team names and spreads
        val homeSpread = gameDetails.spreads.find { it.team == gameDetails.home_team }?.spread ?: "N/A"
        val awaySpread = gameDetails.spreads.find { it.team == gameDetails.away_team }?.spread ?: "N/A"

        findViewById<TextView>(R.id.gameTitleTextView).text = buildString {
            append("${gameDetails.home_team} ($homeSpread) : ${"%.1f".format(gameDetails.home_score_projection)}\n")
            append("vs\n")
            append("${gameDetails.away_team} ($awaySpread) : ${"%.1f".format(gameDetails.away_score_projection)}")
        }
    }

    private fun addComment(commentText: String) {
        val comment = Comment(
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "",
            text = commentText,
            timestamp = System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance().collection("games")
            .document(gameId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener { documentReference ->
                comment.id = documentReference.id // Now this should work since 'id' is a 'var'
                adapter.addComment(comment) // Ensure 'addComment' is defined in CommentAdapter
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
                        DocumentChange.Type.ADDED -> {
                            val comment = dc.document.toObject(Comment::class.java)
                            comment.id = dc.document.id // Set the document ID to the comment's id
                            adapter.addComment(comment)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            // Here you would update the comment in the adapter
                            // This is a TODO because you didn't specify how to handle updates
                            TODO("Handle comment modification")
                        }
                        DocumentChange.Type.REMOVED -> {
                            // Here you would remove the comment from the adapter
                            // This is a TODO because you didn't specify how to handle removals
                            TODO("Handle comment removal")
                        }
                    }
                }
            }
    }
}