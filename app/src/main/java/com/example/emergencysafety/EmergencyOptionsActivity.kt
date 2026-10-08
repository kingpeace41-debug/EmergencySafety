package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmergencyOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_options)

        val btnContactSettings = findViewById<Button>(R.id.btnContactSettings)
        val btnVoiceSettings = findViewById<Button>(R.id.btnVoiceSettings)

        // 1. Çalışan Buton: Rehber ve Mesaj Ayarları
        btnContactSettings?.setOnClickListener {
            val intent = Intent(this, ContactSettingsActivity::class.java)
            startActivity(intent)
        }

        // 2. Henüz Ekranı Yapılmayan Buton: Sesli Komut Ayarları
        btnVoiceSettings?.setOnClickListener {
            Toast.makeText(this, "Sesli komut ayarları henüz hazırlanmadı.", Toast.LENGTH_SHORT).show()
        }
    }
}
