package com.example.emergencysafety

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class VoiceTriggerService : Service() {

    private lateinit var voiceTriggerManager: VoiceTriggerManager

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceWithNotification()

        voiceTriggerManager = VoiceTriggerManager(
            context = this,
            onRedCodeTriggered = {
                val intent = Intent(this, FakeDeadActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
            },
            onRedCodeDeactivated = {
                val deactivateIntent = Intent(FakeDeadActivity.ACTION_DEACTIVATE_RED_CODE)
                sendBroadcast(deactivateIntent)
            }
        )
        voiceTriggerManager.startListening()
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
