package com.example.emergencysafety

import android.app.Service
import android.content.Intent
import android.os.IBinder

class VoiceTriggerService : Service() {

    private lateinit var sirenManager: SirenManager
    private lateinit var voiceManager: VoiceManager

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        voiceManager = VoiceManager(this)

        // Lambda içinde parametre (it/text) kullanmadan doğrudan çağırıyoruz
        voiceManager.startListening {
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
