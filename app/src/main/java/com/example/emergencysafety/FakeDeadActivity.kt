package com.example.emergencysafety

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class FakeDeadActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    
    private val screenHoldDuration: Long = 3000 // Ekrana 3 saniye basılı tutma
    private val volumeHoldDuration: Long = 2000 // Ses tuşlarına 2 saniye basılı tutma

    private var isExecuted = false // İşlemin mükerrer çalışmasını önler

    private val stopSirenRunnable = Runnable {
        if (!isExecuted) {
            isExecuted = true
            triggerSuccessVibration()
            stopSirenAndExit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setContentView(R.layout.activity_fake_dead)

        // 1. EKRANA 3 SANİYE BASILI TUTMA MANTIĞI
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(stopSirenRunnable, screenHoldDuration)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(stopSirenRunnable)
                }
            }
            true
        }
    }

    // 2. FİZİKSEL SES TUŞLARINA (SES AÇMA / KAPAMA) 2 SANİYE BASILI TUTMA MANTIĞI
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event?.repeatCount == 0) { // Tuşa ilk basıldığı an zamanlayıcıyı başlat
                handler.postDelayed(stopSirenRunnable, volumeHoldDuration)
            }
            return true // Ekranda ses panelinin çıkmasını engeller
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            handler.removeCallbacks(stopSirenRunnable) // Tuş 2 saniye dolmadan bırakılırsa zamanlayıcıyı iptal et
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun triggerSuccessVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
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
        // Yanlışlıkla geri tuşuna basılıp acil durum ekranının kapanmasını önler
    }
}
