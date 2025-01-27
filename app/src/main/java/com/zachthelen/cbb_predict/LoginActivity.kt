package com.zachthelen.cbb_predict

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private val sharedPreferences by lazy { getSharedPreferences("UserPrefs", MODE_PRIVATE) }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100 // Request code for Google Sign In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        rememberMeCheckBox = findViewById(R.id.rememberMe)

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            goToHomeActivity()
            return
        }
// Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            // TODO: Implement login logic with DB
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (checkCredentials(username, password)) {
                if (checkCredentials(username, password)) {
                    saveUserLogin(username, rememberMeCheckBox.isChecked)
                    goToHomeActivity()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.signupButton).setOnClickListener {
            showSignupDialog()
        }

        findViewById<Button>(R.id.forgotPasswordButton).setOnClickListener {
            showForgotPasswordDialog()
        }

        // Set up Google Sign-In button click listener
        val googleSignInButton = findViewById<SignInButton>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
            goToHomeActivity()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString("username", null) != null
    }

    private fun saveUserLogin(username: String, rememberMe: Boolean) {
        with(sharedPreferences.edit()) {
            putString("username", if (rememberMe) username else null)
            // If you're using tokens, store them here instead of or in addition to username
            // putString("token", tokenFromServer)
            apply()
        }
    }

    private fun goToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun checkCredentials(email: String, password: String): Boolean {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithEmail:success")
                    saveUserLogin(email, rememberMeCheckBox.isChecked)
                    goToHomeActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        return false // Return false because we handle success asynchronously
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            remove("username")
            // remove("token") or any other stored login information
            apply()
        }
        // Redirect back to login screen
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun showSignupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_signup, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Sign Up")
            .setPositiveButton("Sign Up") { _, _ ->
                val email = dialogView.findViewById<EditText>(R.id.email).text.toString()
                val password = dialogView.findViewById<EditText>(R.id.password).text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginActivity", "createUserWithEmail:success")
                            saveUserLogin(email, rememberMeCheckBox.isChecked)
                            goToHomeActivity()
                        } else {
                            Log.w("LoginActivity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Forgot Password")
            .setPositiveButton("Recover") { _, _ ->
                // TODO: Implement password recovery logic
                val email = dialogView.findViewById<EditText>(R.id.email).text.toString()
                // Here you would send a recovery email
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithCredential:success")
                    saveUserLogin(auth.currentUser?.email ?: "", rememberMeCheckBox.isChecked)
                    goToHomeActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Google Sign In failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}