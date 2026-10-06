package com.example.emergencysafety

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log

class SirenManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private var isFlashing = false
    private var isFlashOn = false

    // Flaş çakar döngüsü (saniyede 4 kez yanıp söner)
    private val flashRunnable = object : Runnable {
        override fun run() {
            if (!isFlashing) return
            try {
                cameraId?.let { id ->
                    isFlashOn = !isFlashOn
                    cameraManager?.setTorchMode(id, isFlashOn)
                }
            } catch (e: Exception) {
                Log.e("SirenManager", "Flaş kontrol hatası: ${e.message}")
            }
            handler.postDelayed(this, 250) // 250 ms aralık
        }
    }

    init {
        initCameraManager()
    }

    private fun initCameraManager() {
        try {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                cameraManager?.getCameraCharacteristics(id)
                    ?.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Kamera servisine erişilemedi: ${e.message}")
        }
    }

    fun startSiren() {
        // 1. Siren Sesini Başlat
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.siren_sound).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                start()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }

        // 2. Flaş Çakar Efektini Başlat
        startFlashing()
    }

    fun stopSiren() {
        // 1. Siren Sesini Durdur
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }

        // 2. Flaş Çakar Efektini Durdur
        stopFlashing()
    }

    private fun startFlashing() {
        if (cameraId == null) return
        isFlashing = true
        handler.removeCallbacks(flashRunnable)
        handler.post(flashRunnable)
    }

    private fun stopFlashing() {
        isFlashing = false
        handler.removeCallbacks(flashRunnable)
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, false)
                isFlashOn = false
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Flaş kapatılamadı: ${e.message}")
        }
    }
}
