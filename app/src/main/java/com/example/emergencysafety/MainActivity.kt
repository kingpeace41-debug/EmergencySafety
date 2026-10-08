package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sesli Komutlar ve Kodlar Butonu
        findViewById<Button>(R.id.btnVoiceCodes)?.setOnClickListener {
            startActivity(Intent(this, VoiceCodesActivity::class.java))
        }

        // Henüz dosyası oluşturulmamış butonlar için geçici bildirimler
        findViewById<Button>(R.id.btnContacts)?.setOnClickListener {
            Toast.makeText(this, "Acil Durum Kişileri ekranı hazırlanıyor", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnSettings)?.setOnClickListener {
            Toast.makeText(this, "Ayarlar ekranı hazırlanıyor", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnFakeDead)?.setOnClickListener {
            Toast.makeText(this, "Sahte Kapanış ekranı hazırlanıyor", Toast.LENGTH_SHORT).show()
        }
    }
}
