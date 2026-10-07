package com.example.emergencysafety

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class VoiceTriggerService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var prefs: EmergencyPreferences
    private var isListening = false

    override fun onCreate() {
        super.onCreate()
        prefs = EmergencyPreferences(this)
        startForegroundServiceNotification()
        initSpeechRecognizer()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "emergency_voice_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Acil Durum Ses Dinleme Servisi",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🚨 Acil Durum Koruması Aktif")
            .setContentText("Sesli güvenlik kodları dinleniyor...")
            .setSmallIcon(android.R.drawable.ic_btn_speak)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1001, notification)
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    // Dinleme esnasında sessizlik veya zaman aşımı olursa yeniden başlat
                    isListening = false
                    startListening()
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.let { words ->
                        for (spokenText in words) {
                            checkVoiceCommand(spokenText)
                        }
                    }
                    // Continuous (Sürekli) dinleme için tekrar başlat
                    startListening()
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            startListening()
        }
    }

    private fun startListening() {
        if (isListening) return
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "TR"))
            }
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.e("VoiceService", "Dinleme başlatılamadı", e)
        }
    }

    private fun checkVoiceCommand(spokenText: String) {
        val code1 = prefs.code1.lowercase(Locale("tr", "TR")).trim()
        val code2 = prefs.code2.lowercase(Locale("tr", "TR")).trim()
        val spoken = spokenText.lowercase(Locale("tr", "TR")).trim()

        Log.d("VoiceService", "Algılanan Ses: $spoken")

        val isCode1Matched = code1.isNotEmpty() && spoken.contains(code1)
        val isCode2Matched = code2.isNotEmpty() && spoken.contains(code2)

        if (isCode1Matched || isCode2Matched) {
            Log.d("VoiceService", "🚨 ACİL DURUM SES KODU ALGILANDI! Mesaj gönderiliyor...")
            WhatsAppSender.sendEmergencyMessageToAll(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        speechRecognizer?.destroy()
        isListening = false
        super.onDestroy()
    }
}
