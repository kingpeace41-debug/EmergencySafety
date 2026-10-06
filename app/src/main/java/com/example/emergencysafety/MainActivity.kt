package com.example.emergencysafety

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val redButton = findViewById<android.view.View>(R.id.redButton)
        val yellowButton = findViewById<android.view.View>(R.id.yellowButton)
        val greenButton = findViewById<android.view.View>(R.id.greenButton)

        redButton.setOnClickListener {
            vibrate()
            // Daha sonra Kırmızı Acil Durum menüsüne geçilecek
        }

        yellowButton.setOnClickListener {
            vibrate()
            // Daha sonra Sarı Uyarı menüsüne geçilecek
        }

        greenButton.setOnClickListener {
            vibrate()
            // Daha sonra Yeşil Bilgi menüsüne geçilecek
        }
    }

    private fun vibrate() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
