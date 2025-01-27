package com.zachthelen.cbb_predict

import android.os.Bundle

class CBBActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_cbb


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.college_baseball)

        // You can add any specific initialization for College Football here
        // For now, since you want it to be essentially blank, nothing additional is needed.
    }
}