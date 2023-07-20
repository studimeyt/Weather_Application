package com.test.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_DELAY: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            // Start the main activity after the delay
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash activity so the user cannot go back to it
        }, SPLASH_SCREEN_DELAY)
    }
}