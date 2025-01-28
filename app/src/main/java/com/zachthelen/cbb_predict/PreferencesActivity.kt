package com.zachthelen.cbb_predict

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class PreferencesActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        loadUserData()
        setupSaveButton()
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            usernameEditText.setText(it.displayName)
            emailEditText.setText(it.email)
            // Note: You cannot retrieve the password directly from Firebase Authentication for security reasons.
        }
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newEmail = emailEditText.text.toString().trim()
            val newPassword = passwordEditText.text.toString().trim()

            updateUserData(newUsername, newEmail, newPassword)
        }
    }

    private fun updateUserData(newUsername: String, newEmail: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            it.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Preferences", "User profile updated.")
                }
            }

            if (newEmail.isNotEmpty() && newEmail != it.email) {
                it.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Log.d("Preferences", "Email updated.")
                    }
                }
            }

            if (newPassword.isNotEmpty()) {
                it.updatePassword(newPassword).addOnCompleteListener { passwordTask ->
                    if (passwordTask.isSuccessful) {
                        Log.d("Preferences", "Password updated.")
                    }
                }
            }
        }
    }
}