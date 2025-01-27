package com.zachthelen.cbb_predict

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : BaseActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("UserPrefs", MODE_PRIVATE) }

    override fun getLayoutResId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.home)

        // Check if user is logged in
//        if (!isUserLoggedIn()) {
//            goToLoginActivity()
//            return
//        }

        // Setup an option for logout in your menu or UI
//        findViewById<Button>(R.id.logoutButton).setOnClickListener {
//            logout()
//        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString("username", null) != null
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            remove("username")
            // remove("token") or any other stored login information
            apply()
        }
        goToLoginActivity()
    }
}