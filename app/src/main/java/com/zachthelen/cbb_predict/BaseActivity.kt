package com.zachthelen.cbb_predict

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {
    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navView: NavigationView
    protected lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startNewActivity(HomeActivity::class.java)
                R.id.nav_cbb -> startNewActivity(MainActivity::class.java)
                R.id.nav_cfb -> startNewActivity(CFBActivity::class.java)
                R.id.nav_cbaseball -> startNewActivity(CBBActivity::class.java) // Corrected from MainActivity to CBBActivity
                R.id.nav_nba -> startNewActivity(NBAActivity::class.java)
                R.id.nav_nfl -> startNewActivity(NFLActivity::class.java)
                R.id.nav_mlb -> startNewActivity(MLBActivity::class.java)
                R.id.nav_preferences -> startNewActivity(PreferencesActivity::class.java)
                R.id.nav_exit -> drawerLayout.closeDrawer(GravityCompat.START)
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected open fun getLayoutResId(): Int {
        return R.layout.activity_home // Default, override in child activities if necessary
    }

    protected fun startNewActivity(activityClass: Class<out Activity>) {
        startActivity(Intent(this, activityClass))
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}