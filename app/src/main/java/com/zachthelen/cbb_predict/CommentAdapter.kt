package com.zachthelen.cbb_predict

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.util.Locale

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<Comment>()

    // ViewHolder class to hold the views for each comment
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.userNameTextView.text = comment.userName
        holder.commentTextView.text = comment.text
        holder.timestampTextView.text = formatTimestamp(comment.timestamp)
    }

    override fun getItemCount() = comments.size

    // Method to add a new comment to the list
    fun addComment(comment: Comment) {
        comments.add(0, comment)
        notifyItemInserted(0)
    }

    // Helper function to format the timestamp
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}