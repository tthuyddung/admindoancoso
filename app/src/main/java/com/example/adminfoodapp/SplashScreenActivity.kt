package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    private var hasNavigated = false // để tránh chạy cả 2 lần (handler + nút)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val nextButton = findViewById<Button>(R.id.next)
        nextButton.setOnClickListener {
            if (!hasNavigated) {
                hasNavigated = true
                val intent = Intent(this, SignActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
