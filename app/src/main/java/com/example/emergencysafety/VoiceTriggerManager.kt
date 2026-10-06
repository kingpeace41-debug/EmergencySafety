package com.example.emergencysafety

import android.app.Service
import android.content.Intent
import android.os.IBinder

class VoiceTriggerService : Service() {

    private lateinit var sirenManager: SirenManager
    private lateinit var voiceTriggerManager: VoiceTriggerManager // Sınıf ismi VoiceTriggerManager olarak düzeltildi

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        voiceTriggerManager = VoiceTriggerManager(this)

        // Ses algılandığında Kırmızı Kod / Siren başlatılır
        voiceTriggerManager.startListening {
            triggerRedCode()
        }
    }

    private fun triggerRedCode() {
        sirenManager.startSiren()
    }

    override fun onDestroy() {
        super.onDestroy()
        sirenManager.stopSiren()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
