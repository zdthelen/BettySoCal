package com.zachthelen.cbb_predict

import android.os.Bundle

class MLBActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_mlb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.baseball)

        // You can add any specific initialization for College Football here
        // For now, since you want it to be essentially blank, nothing additional is needed.
    }
}
