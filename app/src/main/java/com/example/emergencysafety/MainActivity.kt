package com.example.emergencysafety

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
            vibrate()
            // Kırmızı Acil Durum menüsü buraya eklenecek
        }

        yellowButton.setOnClickListener {
            vibrate()
            // Sarı Uyarı menüsü buraya eklenecek
        }

        blueButton.setOnClickListener {
            vibrate()
            // Mavi Bilgi menüsü buraya eklenecek
        }
    }

    private fun vibrate() {

        val vibrator: Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

            vibrator = vibratorManager.defaultVibrator

        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
