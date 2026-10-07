package com.example.emergencysafety

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // XML tarafındaki metin kutularını (EditText) güvenli şekilde buluyoruz
        val etName = findEditText("etContactName", "etName", "etEmergencyName")
        val etPhone = findEditText("etContactPhone", "etPhone", "etEmergencyPhone")
        val btnSave = findViewById<Button>(R.id.btnSave)

        // 1. ÖNCEDEN KAYDEDİLMİŞ BİLGİLERİ HAFIZADAN OKU VE EKRANA YAZ
        val savedName = sharedPref.getString("emergency_name", "")
        val savedPhone = sharedPref.getString("emergency_phone", "")

        etName?.setText(savedName)
        etPhone?.setText(savedPhone)

        // 2. KAYDET VE ÇIK BUTONUNA BASILDIGINDA HAFIZAYA KAYDET
        btnSave?.setOnClickListener {
            val nameInput = etName?.text?.toString()?.trim() ?: ""
            val phoneInput = etPhone?.text?.toString()?.trim() ?: ""

            // Bilgileri kalıcı olarak kaydet
            sharedPref.edit().apply {
                putString("emergency_name", nameInput)
                putString("emergency_phone", phoneInput)
                apply()
            }

            Toast.makeText(this, "Acil durum kişisi kaydedildi!", Toast.LENGTH_SHORT).show()
            finish() // Menüden çıkıp ana sayfaya döner
        }
    }

    // FARKLI ID İSİMLERİNE KARŞI ESNEK BULMA FONKSİYONU
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
