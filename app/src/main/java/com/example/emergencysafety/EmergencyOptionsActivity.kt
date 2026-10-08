package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class EmergencyOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_options)

        // 1. Rehber ve Mesaj Ayarları Butonu (Henüz ekranını yazmadığımız için şimdilik boş/pasif)
        val btnContactSettings = findViewById<View>(R.id.btnContactSettings)
        btnContactSettings?.setOnClickListener {
            // Adım 2'de burayı yönlendireceğiz
        }

        // 2. Sesli Komut Ayarları Butonu (Mevcut çalışan SettingsActivity'nize gider)
        val btnVoiceSettings = findViewById<View>(R.id.btnVoiceSettings)
        btnVoiceSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
