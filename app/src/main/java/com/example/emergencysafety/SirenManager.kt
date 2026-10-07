package com.example.emergencysafety

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.util.Log

class SirenManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isSirenRunning = false

    fun startSiren() {
        if (isSirenRunning) return
        isSirenRunning = true

        // 1. Ses Çalma (Alarm Sesi)
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alarmUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Siren sesi başlatılamadı: ${e.message}")
        }

        // 2. Titreşim
        try {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 500, 200, 500)
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 200, 500), 0)
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Titreşim başlatılamadı: ${e.message}")
        }

        // 3. Flaş Açma
        try {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager?.setTorchMode(cameraId!!, true)
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Flaş açılamadı: ${e.message}")
        }
    }

    fun stopSiren() {
        if (!isSirenRunning) return
        isSirenRunning = false

        // Sesi Durdur
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("SirenManager", "Siren durdurulamadı: ${e.message}")
        }

        // Titreşimi Durdur
        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            Log.e("SirenManager", "Titreşim durdurulamadı: ${e.message}")
        }

        // Flaş Kapat
        try {
            if (cameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager?.setTorchMode(cameraId!!, false)
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Flaş kapatılamadı: ${e.message}")
        }
    }
}
