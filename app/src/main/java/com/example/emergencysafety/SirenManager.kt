package com.example.emergencysafety

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log

class SirenManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashOn = false
    private var flashThread: Thread? = null
    @Volatile private var isSirenRunning = false

    init {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager?.cameraIdList?.firstOrNull()
        } catch (e: Exception) {
            Log.e("SirenManager", "Kamera erişim hatası: ${e.message}")
        }
    }

    fun startSiren() {
        if (isSirenRunning) return
        isSirenRunning = true

        // 1. Alarm Sesi
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alertUri)
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
            Log.e("SirenManager", "Ses hatası: ${e.message}")
        }

        // 2. Flaş Çakar
        startFlashBlinking()
    }

    private fun startFlashBlinking() {
        if (cameraId == null) return

        flashThread = Thread {
            try {
                while (isSirenRunning) {
                    toggleFlash(!isFlashOn)
                    Thread.sleep(200) // 200 milisaniyede bir yak/söndür
                }
            } catch (e: Exception) {
                Log.e("SirenManager", "Flaş hatası: ${e.message}")
            } finally {
                toggleFlash(false)
            }
        }
        flashThread?.start()
    }

    private fun toggleFlash(enable: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && cameraId != null) {
                cameraManager?.setTorchMode(cameraId!!, enable)
                isFlashOn = enable
            }
        } catch (e: Exception) {
            Log.e("SirenManager", "Torch hatası: ${e.message}")
        }
    }

    fun stopSiren() {
        isSirenRunning = false

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("SirenManager", "Ses durdurma hatası: ${e.message}")
        }

        toggleFlash(false)
        flashThread?.interrupt()
        flashThread = null
    }

    fun isRunning(): Boolean = isSirenRunning
}
