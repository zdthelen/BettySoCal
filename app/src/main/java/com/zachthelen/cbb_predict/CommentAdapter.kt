package com.zachthelen.cbb_predict

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale

class CommentAdapter(private val comments: MutableList<Comment>, private val gameId: String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    fun addComment(comment: Comment) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentText: TextView = itemView.findViewById(R.id.commentText)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestamp)

        fun bind(comment: Comment) {
            commentText.text = comment.text
            username.text = comment.userName
            timestampTextView.text = formatTimestamp(comment.timestamp)

            itemView.setOnClickListener {
                showCommentOptions(comment)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
            return sdf.format(date)
        }

        private fun showCommentOptions(comment: Comment) {
            val popup = PopupMenu(itemView.context, itemView)
            popup.menuInflater.inflate(R.menu.comment_options, popup.menu)

            canDeleteComment(comment) { canDelete ->
                val deleteItem = popup.menu.findItem(R.id.action_delete)
                deleteItem.isVisible = canDelete

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_delete -> {
                            deleteComment(comment)
                            true
                        }
                        R.id.action_back -> {
                            // Close the popup, no action needed
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }



        private fun canDeleteComment(comment: Comment, callback: (Boolean) -> Unit) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser?.uid == comment.userId) {
                callback(true)
            } else {
                isDeveloper(currentUser?.uid) { isDev ->
                    callback(isDev)
                }
            }
        }

        private fun isDeveloper(userId: String?, callback: (Boolean) -> Unit) {
            if (userId == null) {
                callback(false)
                return
            }
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    callback(document.getBoolean("isDeveloper") ?: false)
                }
                .addOnFailureListener {
                    callback(false) // In case of failure, assume not a developer
                }
        }

        private fun deleteComment(comment: Comment) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("games")
                .document(gameId)
                .collection("comments")
                .document(comment.id)
                .delete()
                .addOnSuccessListener {
                    comments.remove(comment)
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("CommentAdapter", "Error deleting document", e)
                }
        }
    }
}