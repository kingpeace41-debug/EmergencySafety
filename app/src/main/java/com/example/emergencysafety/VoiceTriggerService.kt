package com.example.emergencysafety

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class VoiceTriggerService : Service() {

    private lateinit var sirenManager: SirenManager
    private lateinit var voiceTriggerManager: VoiceTriggerManager

    override fun onCreate() {
        super.onCreate()
        sirenManager = SirenManager(this)
        voiceTriggerManager = VoiceTriggerManager(this)

        startForegroundServiceWithNotification()

        voiceTriggerManager.startListening(
            onRedStart = { triggerRedCode() },
            onRedStop = { stopRedCode() },
            onSirenStart = { triggerSirenCode() },
            onSirenStop = { stopSirenCode() }
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_STOP_SIREN" -> stopSirenCode()
            "ACTION_STOP_RED" -> stopRedCode()
        }
        return START_STICKY
    }

    private fun triggerRedCode() {
        // 1. Ayarlarda Kayıtlı Kişiye Mesaj ve Konum Gönder
        sendEmergencySms()

        // 2. Acil Durum / Fake Dead Ekranını Başlat
        val intent = Intent(this, FakeDeadActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }

    private fun sendEmergencySms() {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val phone = sharedPref.getString("emergency_phone", "") ?: ""
        val customMessage = sharedPref.getString(
            "emergency_message",
            "Acil durum oluştu, yardıma ihtiyacım var! Lütfen bana ulaşmaya çalışın."
        ) ?: "Acil durum oluştu, yardıma ihtiyacım var! Lütfen bana ulaşmaya çalışın."

        // Telefon numarası girilmediyse işlem yapılmaz
        if (phone.isEmpty()) {
            return
        }

        // SMS İzni Kontrolü
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Anlık Konum Bağlantısını Oluştur
        val locationLink = getLocationLink()
        val fullSms = if (locationLink != null) {
            "$customMessage\n\nAnlık Konumum:\n$locationLink"
        } else {
            "$customMessage\n\n(Konum bilgisi alınamadı)"
        }

        // SMS Gönderme
        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            // Uzun mesajların bölünerek eksiksiz ulaşmasını sağlar
            val parts = smsManager.divideMessage(fullSms)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phone, null, fullSms, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocationLink(): String? {
        val hasFineLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLoc && !hasCoarseLoc) return null

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var bestLocation: Location? = null

        try {
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (gpsLocation != null && networkLocation != null) {
                bestLocation = if (gpsLocation.time > networkLocation.time) gpsLocation else networkLocation
            } else {
                bestLocation = gpsLocation ?: networkLocation
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return bestLocation?.let {
            "https://maps.google.com/?q=${it.latitude},${it.longitude}"
        }
    }

    private fun stopRedCode() {
        val stopBroadcast = Intent("com.example.emergencysafety.CLOSE_FAKE_DEAD").apply {
            setPackage(packageName)
        }
        sendBroadcast(stopBroadcast)

        val closeIntent = Intent(this, FakeDeadActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("EXTRA_CLOSE", true)
        }
        startActivity(closeIntent)
    }

    private fun triggerSirenCode() {
        sirenManager.startSiren()
    }

    private fun stopSirenCode() {
        sirenManager.stopSiren()
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "voice_trigger_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Ses Algılama Servisi",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Emergency Safety Aktif")
            .setContentText("Ses komutları dinleniyor...")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceTriggerManager.stopListening()
        sirenManager.stopSiren()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
