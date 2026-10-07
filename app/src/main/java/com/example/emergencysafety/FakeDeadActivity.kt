package com.example.emergencysafety

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class FakeDeadActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ekranı tam ekran ve kilit üstünde gösterilecek şekilde yapılandır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Ekran parlaklığını sıfıra yakın yap (Kararma efekti)
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.01f
        window.attributes = layoutParams

        val rootLayout = View(this).apply {
            setBackgroundColor(Color.BLACK)
        }
        setContentView(rootLayout)

        setupTouchListener(rootLayout)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(closeReceiver, IntentFilter("com.example.emergencysafety.CLOSE_FAKE_DEAD"), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(closeReceiver, IntentFilter("com.example.emergencysafety.CLOSE_FAKE_DEAD"))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener(view: View) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    longPressRunnable = Runnable {
                        stopAlarmAndClose()
                    }
                    handler.postDelayed(longPressRunnable!!, 3000) // 3 saniye basılı tutma
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    true
                }
                else -> false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            stopAlarmAndClose()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun stopAlarmAndClose() {
        val serviceIntent = Intent(this, VoiceTriggerService::class.java).apply {
            action = "ACTION_STOP_ALARM"
        }
        startService(serviceIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(closeReceiver)
        } catch (e: Exception) {
            // Unregistered
        }
    }
}
