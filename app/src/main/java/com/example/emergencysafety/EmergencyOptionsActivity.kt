package com.example.emergencysafety

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

        // 1. Rehber Ayarları Butonu (Geri tuşu akışı korundu)
        btnContactSettings?.setOnClickListener {
            Toast.makeText(this, "Rehber ve Mesaj Ayarları ekranına geçiliyor...", Toast.LENGTH_SHORT).show()
        }

        // 2. Sesli Komut Ayarları Butonu
        btnVoiceSettings?.setOnClickListener {
            Toast.makeText(this, "Sesli komut ayarları henüz hazır değil.", Toast.LENGTH_SHORT).show()
        }
    }
}
