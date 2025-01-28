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
        gameDetails = intent.getParcelableExtra("GAME_DETAILS") ?: return

        // Use gameDetails to set up the UI with specific game information
        setupGameDetailsUI()

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

//    private fun setupGameDetailsUI() {
//        // Assuming you have UI elements to display game details
//        findViewById<TextView>(R.id.gameTitleTextView).text = "${gameDetails.home_team} vs ${gameDetails.away_team}"
//        // Add more UI elements as needed, like projections, spreads, etc.
//        findViewById<TextView>(R.id.homeProjectionTextView).text = "Home Points: ${"%.1f".format(gameDetails.home_score_projection)}"
//        findViewById<TextView>(R.id.awayProjectionTextView).text = "Away Points: ${"%.1f".format(gameDetails.away_score_projection)}"
//
//        // Example for displaying spreads:
//        gameDetails.spreads.forEach { spread ->
//            val spreadText = if (spread.team == gameDetails.home_team) {
//                "Home Spread: ${spread.spread}"
//            } else {
//                "Away Spread: ${spread.spread}"
//            }
//            // You might want to add this to a TextView, or if you have multiple, perhaps to a list or layout
//            findViewById<TextView>(R.id.spreadInfoTextView).append("$spreadText\n")
//        }
//    }

    private fun setupGameDetailsUI() {
        // Game Title with team names and spreads
        val homeSpread =
            gameDetails.spreads.find { it.team == gameDetails.home_team }?.spread ?: "N/A"
        val awaySpread =
            gameDetails.spreads.find { it.team == gameDetails.away_team }?.spread ?: "N/A"

        findViewById<TextView>(R.id.gameTitleTextView).text =
            buildString {
                append("${gameDetails.home_team} ($homeSpread) : ${"%.1f".format(gameDetails.home_score_projection)}\n")
                append("vs\n")
                append("${gameDetails.away_team} ($awaySpread) : ${"%.1f".format(gameDetails.away_score_projection)}")
            }
    }


        // The rest of your functions remain largely the same, but now you have gameDetails available

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