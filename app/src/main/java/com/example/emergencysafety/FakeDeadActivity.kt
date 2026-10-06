package com.example.emergencysafety

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class FakeDeadActivity : AppCompatActivity() {

    private var topLeftTapCount = 0
    private var bottomRightTapCount = 0

    private val exitReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DEACTIVATE_RED_CODE) {
                restoreScreenAndReturnHome()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_dead)
        
        setupFullScreenOverlay()

        val filter = IntentFilter(ACTION_DEACTIVATE_RED_CODE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(exitReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(exitReceiver, filter)
        }
    }

    private fun setupFullScreenOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.0f
        window.attributes = layoutParams

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    // YEDEK GİZLİ ŞİFRE: Sol üst köşeye 2 kez, ardından Sağ alt köşeye 1 kez dokunulursa açılır.
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels

            // Sol üst köşe alanı (%25lik bölge)
            if (x < screenWidth * 0.25f && y < screenHeight * 0.25f) {
                topLeftTapCount++
                if (topLeftTapCount > 2) topLeftTapCount = 1
            } 
            // Sağ alt köşe alanı (%25lik bölge)
            else if (x > screenWidth * 0.75f && y > screenHeight * 0.75f) {
                if (topLeftTapCount >= 2) {
                    bottomRightTapCount++
                    if (bottomRightTapCount >= 1) {
                        restoreScreenAndReturnHome()
                    }
                } else {
                    topLeftTapCount = 0
                    bottomRightTapCount = 0
                }
            } else {
                topLeftTapCount = 0
                bottomRightTapCount = 0
            }
        }
        return super.onTouchEvent(event)
    }

    private fun restoreScreenAndReturnHome() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = layoutParams

        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(exitReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Geri tuşunu kilitler
    }

    companion object {
        const val ACTION_DEACTIVATE_RED_CODE = "com.example.emergencysafety.DEACTIVATE_RED_CODE"
    }
}
