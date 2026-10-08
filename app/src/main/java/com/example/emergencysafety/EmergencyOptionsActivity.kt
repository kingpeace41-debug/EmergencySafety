package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class EmergencyOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_options)

        // 1. Rehber ve Mesaj Ayarları Butonu (2. Adımda sayfasını oluşturunca dolduracağız)
        val btnContactSettings = findViewById<View>(R.id.btnContactSettings)
        btnContactSettings?.setOnClickListener {
            // Şimdilik boş bırakıyoruz, derleme hatasını engellemek için
        }

        // 2. Sesli Komut Ayarları Butonu
        val btnVoiceSettings = findViewById<View>(R.id.btnVoiceSettings)
        btnVoiceSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
