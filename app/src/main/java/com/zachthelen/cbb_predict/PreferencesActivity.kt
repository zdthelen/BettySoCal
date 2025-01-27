package com.zachthelen.cbb_predict

import android.os.Bundle

class PreferencesActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // You can add any specific initialization for College Football here
        // For now, since you want it to be essentially blank, nothing additional is needed.
    }
}