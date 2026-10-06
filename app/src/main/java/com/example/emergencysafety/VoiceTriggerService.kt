package com.example.emergencysafety

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class VoiceTriggerService : Service() {

    private var voiceTriggerManager: VoiceTriggerManager? = null
    private var sirenManager: SirenManager? = null

    // "Siren 41" güvenlik sayacı ve zaman takibi
    private var sirenTriggerCount = 0
    private var lastSirenTriggerTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        startForegroundService()
        setupVoiceRecognition()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_STOP_SIREN") {
            sirenManager?.stopSiren()
            sirenTriggerCount = 0 // Sayacı sıfırla
        }
        return START_STICKY
    }

    private fun setupVoiceRecognition() {
        voiceTriggerManager = VoiceTriggerManager(this) { command ->
            val normalizedCommand = command.lowercase().replace(" ", "")

            // "siren41" veya "siren 41" komutunu kontrol et
            if (normalizedCommand.contains("siren41") || command.contains("siren 41", ignoreCase = true)) {
                val currentTime = System.currentTimeMillis()

                // İlk söyleyişin üzerinden 10 saniyeden fazla geçtiyse sayacı sıfırla
                if (currentTime - lastSirenTriggerTime > 10000) {
                    sirenTriggerCount = 0
                }

                sirenTriggerCount++
                lastSirenTriggerTime = currentTime

                // "Siren 41" üst üste 2 defa algılandığında sireni ve ekranı başlat
                if (sirenTriggerCount >= 2) {
                    sirenTriggerCount = 0 // Tetiklendi, sayacı sıfırla
                    triggerVibration()
                    sirenManager?.startSiren()

                    val intent = Intent(this, FakeDeadActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(intent)
                }
            }
        }
        voiceTriggerManager?.startListening()
    }

    private fun triggerVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(300)
            }
        }
    }

    private fun startForegroundService() {
        val channelId = "emergency_safety_service"
        val channelName = "Emergency Safety Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Emergency Safety Aktif")
            .setContentText("Sesli komutlar ve güvenlik arka planda dinleniyor.")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceTriggerManager?.stopListening()
        sirenManager?.stopSiren()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
