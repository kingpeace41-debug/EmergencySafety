package com.example.emergencysafety

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val redButton = findViewById<View>(R.id.redButton)
        val yellowButton = findViewById<View>(R.id.yellowButton)
        val greenButton = findViewById<View>(R.id.greenButton)

        redButton.setOnClickListener {
            // Kırmızı iç menü
        }

        yellowButton.setOnClickListener {
            // Sarı iç menü
        }

        greenButton.setOnClickListener {
            // Yeşil iç menü
        }
    }
}
