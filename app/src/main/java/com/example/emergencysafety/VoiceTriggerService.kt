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

    private lateinit var voiceTriggerManager: VoiceTriggerManager

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceWithNotification()

        voiceTriggerManager = VoiceTriggerManager(
            context = this,
            onRedCodeTriggered = {
                vibrateFeedback(isActivation = true)
                val intent = Intent(this, FakeDeadActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
            },
            onRedCodeDeactivated = {
                vibrateFeedback(isActivation = false)

                // 1. Yayın Gönder (Paket ismi eklenerek Android 13+ engeli aşıldı)
                val deactivateIntent = Intent(FakeDeadActivity.ACTION_DEACTIVATE_RED_CODE).apply {
                    setPackage(packageName)
                }
                sendBroadcast(deactivateIntent)

                // 2. Yedek olarak doğrudan MainActivity'yi çağır ve siyah ekranı kapat
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(mainIntent)
            }
        )
        voiceTriggerManager.startListening()
    }

    private fun vibrateFeedback(isActivation: Boolean) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (isActivation) {
                // Kırmızı Kod Açılışı: 2 Kısa Titreşim
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 150, 100, 150), -1)
                }
            } else {
                // Kırmızı Kod İptali: 1 Uzun Titreşim
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "EmergencySafetyServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Güvenlik Koruma Servisi",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Emergency Safety Aktif")
            .setContentText("Güvenlik koruması arka planda çalışıyor.")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1001, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        voiceTriggerManager.stopListening()
    }
}
