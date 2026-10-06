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

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        startForegroundService()
        setupVoiceRecognition()
    }

    private fun setupVoiceRecognition() {
        voiceTriggerManager = VoiceTriggerManager(this) { command ->
            triggerVibration()
            
            // "siren" kelimesi veya acil durum komutu algılandığında siren çal
            if (command.contains("siren", ignoreCase = true) || command.contains("kırmızı", ignoreCase = true)) {
                sirenManager?.startSiren()
            }

            // Siyah ekranı / Acil durum ekranını başlat
            val intent = Intent(this, FakeDeadActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
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
