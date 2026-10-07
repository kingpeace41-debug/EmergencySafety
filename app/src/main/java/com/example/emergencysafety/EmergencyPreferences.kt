package com.example.emergencysafety

import android.content.Context
import android.content.SharedPreferences

class EmergencyPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("EmergencyAppPrefs", Context.MODE_PRIVATE)

    // --- SES KODLARI ---
    var emergencyCode: String
        get() = prefs.getString("emergency_code", "kırmızı 41") ?: "kırmızı 41"
        set(value) = prefs.edit().putString("emergency_code", value.lowercase()).apply()

    var emergencyCancelCode: String
        get() = prefs.getString("emergency_cancel_code", "kırmızı iptal") ?: "kırmızı iptal"
        set(value) = prefs.edit().putString("emergency_cancel_code", value.lowercase()).apply()

    var sirenCode: String
        get() = prefs.getString("siren_code", "siren aç") ?: "siren aç"
        set(value) = prefs.edit().putString("siren_code", value.lowercase()).apply()

    var sirenCancelCode: String
        get() = prefs.getString("siren_cancel_code", "siren kapat") ?: "siren kapat"
        set(value) = prefs.edit().putString("siren_cancel_code", value.lowercase()).apply()

    // --- ACİL DURUM KİŞİLERİ ---
    var contact1Name: String
        get() = prefs.getString("contact_1_name", "") ?: ""
        set(value) = prefs.edit().putString("contact_1_name", value).apply()

    var contact1Number: String
        get() = prefs.getString("contact_1_num", "") ?: ""
        set(value) = prefs.edit().putString("contact_1_num", value).apply()

    var contact2Name: String
        get() = prefs.getString("contact_2_name", "") ?: ""
        set(value) = prefs.edit().putString("contact_2_name", value).apply()

    var contact2Number: String
        get() = prefs.getString("contact_2_num", "") ?: ""
        set(value) = prefs.edit().putString("contact_2_num", value).apply()

    var contact3Name: String
        get() = prefs.getString("contact_3_name", "") ?: ""
        set(value) = prefs.edit().putString("contact_3_name", value).apply()

    var contact3Number: String
        get() = prefs.getString("contact_3_num", "") ?: ""
        set(value) = prefs.edit().putString("contact_3_num", value).apply()

    // --- ORTAK MESAJ METNİ ---
    var sharedMessage: String
        get() = prefs.getString("shared_message", "🚨 ACİL DURUM! Yardıma ihtiyacım var! Konum bilgim ekte gönderilmiştir.") ?: ""
        set(value) = prefs.edit().putString("shared_message", value).apply()

    // Kayıtlı telefon numaralarını liste olarak döndürür
    fun getActiveContactNumbers(): List<String> {
        val list = mutableListOf<String>()
        if (contact1Number.isNotBlank()) list.add(contact1Number)
        if (contact2Number.isNotBlank()) list.add(contact2Number)
        if (contact3Number.isNotBlank()) list.add(contact3Number)
        return list
    }
}
