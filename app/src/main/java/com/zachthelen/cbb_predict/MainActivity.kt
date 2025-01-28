package com.zachthelen.cbb_predict

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_main

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.college_basketball)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val baseUrl = "http://192.168.1.173:5000/"
        Log.d("MainActivity", "Base URL: $baseUrl")
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(MatchupApi::class.java)

        lifecycleScope.launch {
            try {
                // GET the processed data from /store_matchups
                val projections = api.getProjections()
                Log.d("MainActivity", "Stored projections size: ${projections.size}")
                adapter = MatchupAdapter(projections) { gameId, gameDetails ->
                    val intent = Intent(this@MainActivity, GameCommentActivity::class.java)
                    intent.putExtra("GAME_ID", gameId)
                    intent.putExtra("GAME_DETAILS", gameDetails)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching stored projections", e)
            }
        }
    }
}
