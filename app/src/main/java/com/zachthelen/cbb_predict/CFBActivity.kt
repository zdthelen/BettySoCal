package com.zachthelen.cbb_predict

import android.os.Bundle

class CFBActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_cfb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.college_football)

        // You can add any specific initialization for College Football here
        // For now, since you want it to be essentially blank, nothing additional is needed.
    }
}