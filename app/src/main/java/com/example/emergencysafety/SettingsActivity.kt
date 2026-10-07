package com.example.emergencysafety

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private val defaultMessage = "Acil durum oluştu, yardıma ihtiyacım var! Lütfen bana ulaşmaya çalışın."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val etName = findEditText("etContactName", "etName", "etEmergencyName")
        val etPhone = findEditText("etContactPhone", "etPhone", "etEmergencyPhone")
        val etMessage = findEditText("etEmergencyMessage", "etMessage", "etHelpMessage")
        val btnSave = findViewById<Button>(R.id.btnSave)

        // 1. DAHÖ ÖNCE KAYDEDİLEN BİLGİLERİ VE MESAJI YÜKLE
        val savedName = sharedPref.getString("emergency_name", "")
        val savedPhone = sharedPref.getString("emergency_phone", "")
        val savedMessage = sharedPref.getString("emergency_message", defaultMessage)

        etName?.setText(savedName)
        etPhone?.setText(savedPhone)
        etMessage?.setText(savedMessage)

        // 2. KAYDET VE ÇIK BUTONUNA BASILDIĞINDA
        btnSave?.setOnClickListener {
            val nameInput = etName?.text?.toString()?.trim() ?: ""
            val phoneInput = etPhone?.text?.toString()?.trim() ?: ""
            var messageInput = etMessage?.text?.toString()?.trim() ?: ""

            if (messageInput.isEmpty()) {
                messageInput = defaultMessage
            }

            // Hafızaya Kalıcı Olarak Kaydet
            sharedPref.edit().apply {
                putString("emergency_name", nameInput)
                putString("emergency_phone", phoneInput)
                putString("emergency_message", messageInput)
                apply()
            }

            Toast.makeText(this, "Acil durum ayarları kaydedildi!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun findEditText(vararg possibleIds: String): EditText? {
        for (idName in possibleIds) {
            val id = resources.getIdentifier(idName, "id", packageName)
            if (id != 0) {
                val editText = findViewById<EditText>(id)
                if (editText != null) return editText
            }
        }
        return null
    }
}
