package com.example.emergencysafety

import android.app.Service
import android.content.Intent
import android.os.IBinder

class VoiceTriggerService : Service() {

    private lateinit var voiceManager: VoiceManager // Veya ses dinleme sınıfınız
    private lateinit var sirenManager: SirenManager

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        voiceManager = VoiceManager(this)

        // 41. Satırdaki düzeltilmiş dinleyici çağrısı:
        voiceManager.startListening {
            // Ses tetiklendiğinde çalışacak kodlar
            triggerRedCode()
        }
    }

    private fun triggerRedCode() {
        sirenManager.startSiren()
        // Acil durum bildirim / SMS gönderme işlemleri
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
