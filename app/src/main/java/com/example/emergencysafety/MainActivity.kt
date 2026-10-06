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
        val blueButton = findViewById<View>(R.id.blueButton)

        redButton.setOnClickListener {
            // Kırmızı Acil Durum menüsü
        }

        yellowButton.setOnClickListener {
            // Sarı Uyarı menüsü
        }

        blueButton.setOnClickListener {
            // Mavi Bilgi menüsü
        }
    }
}
