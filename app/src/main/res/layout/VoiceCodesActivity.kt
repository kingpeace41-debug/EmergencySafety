package com.example.emergencysafety

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VoiceCodesActivity : AppCompatActivity() {

    private lateinit var prefs: EmergencyPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_codes)

        prefs = EmergencyPreferences(this)

        val etEmergencyCode = findViewById<EditText>(R.id.etEmergencyCode)
        val etEmergencyCancelCode = findViewById<EditText>(R.id.etEmergencyCancelCode)
        val etSirenCode = findViewById<EditText>(R.id.etSirenCode)
        val etSirenCancelCode = findViewById<EditText>(R.id.etSirenCancelCode)
        val btnSave = findViewById<Button>(R.id.btnSaveVoiceCodes)

        // Mevcut kayıtlı kodları ekran açıldığında doldur
        etEmergencyCode.setText(prefs.emergencyCode)
        etEmergencyCancelCode.setText(prefs.emergencyCancelCode)
        etSirenCode.setText(prefs.sirenCode)
        etSirenCancelCode.setText(prefs.sirenCancelCode)

        // Kaydet Butonuna Basıldığında
        btnSave.setOnClickListener {
            val emergency = etEmergencyCode.text.toString().trim()
            val emergencyCancel = etEmergencyCancelCode.text.toString().trim()
            val siren = etSirenCode.text.toString().trim()
            val sirenCancel = etSirenCancelCode.text.toString().trim()

            if (emergency.isEmpty() || emergencyCancel.isEmpty() || siren.isEmpty() || sirenCancel.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm ses kodlarını doldurun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.emergencyCode = emergency
            prefs.emergencyCancelCode = emergencyCancel
            prefs.sirenCode = siren
            prefs.sirenCancelCode = sirenCancel

            Toast.makeText(this, "Ses kodları başarıyla kaydedildi! ✅", Toast.LENGTH_SHORT).show()
            finish() // Sayfayı kapatıp iç menüye geri dön
        }
    }
}
