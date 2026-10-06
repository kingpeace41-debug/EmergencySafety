package com.example.emergencysafety

import android.os.Bundle
import android.view.HapticFeedbackConstants
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
            vibrate(redButton)
            // Kırmızı Acil Durum menüsü buraya eklenecek
        }

        yellowButton.setOnClickListener {
            vibrate(yellowButton)
            // Sarı Uyarı menüsü buraya eklenecek
        }

        blueButton.setOnClickListener {
            vibrate(blueButton)
            // Mavi Bilgi menüsü buraya eklenecek
        }
    }

    private fun vibrate(view: View) {
        view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY
        )
    }
}
