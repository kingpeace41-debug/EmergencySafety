package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InnerMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inner_menu)

        val btnVoiceCodes = findViewById<Button>(R.id.btnVoiceCodes)
        val btnEmergencyContacts = findViewById<Button>(R.id.btnEmergencyContacts)

        // 1. Buton: Ses Kodları Menüsü
        btnVoiceCodes.setOnClickListener {
            val intent = Intent(this, VoiceCodesActivity::class.java)
            startActivity(intent)
        }

        // 2. Buton: İletişim Kişileri ve Mesaj Menüsü
        btnEmergencyContacts.setOnClickListener {
            Toast.makeText(this, "Kişi Kayıt ve Mesaj Ayarları Sayfasına Geçilecek...", Toast.LENGTH_SHORT).show()
            // Sıradaki adımda burayı da tanımlayacağız
        }
    }
}
