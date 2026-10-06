package com.example.emergencysafety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var voiceTriggerManager: VoiceTriggerManager
    private val RECORD_AUDIO_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buton Tanımlamaları ve Dokunsal Geri Bildirim
        val redButton = findViewById<View>(R.id.redButton)
        val yellowButton = findViewById<View>(R.id.yellowButton)
        val blueButton = findViewById<View>(R.id.blueButton)

        redButton?.setOnClickListener {
            vibrate(it)
            // Kırmızı Acil Durum manuel tetikleme alanı
        }

        yellowButton?.setOnClickListener {
            vibrate(it)
            // Sarı Uyarı menüsü alanı
        }

        blueButton?.setOnClickListener {
            vibrate(it)
            // Mavi Bilgi menüsü alanı
        }

        // Ses Tanıma Yöneticisi Kurulumu
        voiceTriggerManager = VoiceTriggerManager(
            context = this,
            onRedCodeTriggered = {
                Toast.makeText(this, "KIRMIZI KOD TETİKLENDİ!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, FakeDeadActivity::class.java)
                startActivity(intent)
            },
            onRedCodeDeactivated = {
                Toast.makeText(this, "Sistem Güvenli Moda Döndü", Toast.LENGTH_SHORT).show()
                val deactivateIntent = Intent(FakeDeadActivity.ACTION_DEACTIVATE_RED_CODE)
                sendBroadcast(deactivateIntent)
            }
        )

        // Mikrofon İzin Kontrolü ve Dinlemeyi Başlatma
        checkAudioPermission()
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        } else {
            voiceTriggerManager.startListening()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            voiceTriggerManager.startListening()
        }
    }

    private fun vibrate(view: View) {
        view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceTriggerManager.stopListening()
    }
}
