package com.example.emergencysafety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val RECORD_AUDIO_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restoreScreenBrightness()

        val redButton = findViewById<View>(R.id.redButton)
        val yellowButton = findViewById<View>(R.id.yellowButton)
        val blueButton = findViewById<View>(R.id.blueButton)

        redButton?.setOnClickListener { vibrate(it) }
        yellowButton?.setOnClickListener { vibrate(it) }
        blueButton?.setOnClickListener { vibrate(it) }

        checkAudioPermissionAndStartService()
    }

    override fun onResume() {
        super.onResume()
        restoreScreenBrightness()
    }

    private fun restoreScreenBrightness() {
        // Ekran parlaklığını sistemin varsayılan değerine zorla sıfırla
        val layoutParams = window.attributes
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = layoutParams
    }

    private fun checkAudioPermissionAndStartService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        } else {
            startVoiceService()
        }
    }

    private fun startVoiceService() {
        val serviceIntent = Intent(this, VoiceTriggerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceService()
        }
    }

    private fun vibrate(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}
