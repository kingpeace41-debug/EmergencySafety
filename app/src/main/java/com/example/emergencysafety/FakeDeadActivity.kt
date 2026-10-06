package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class FakeDeadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setContentView(R.layout.activity_fake_dead)

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnLongClickListener {
            stopSirenAndExit()
            true
        }
    }

    private fun stopSirenAndExit() {
        val stopIntent = Intent(this, VoiceTriggerService::class.java).apply {
            action = "ACTION_STOP_SIREN"
        }
        startService(stopIntent)
        finish()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Yanlışlıkla geri tuşuna basılıp ekranın kapanmasını önler
    }
}
