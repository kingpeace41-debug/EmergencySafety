package com.example.emergencysafety

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri

class SirenManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var originalVolume: Int = 0

    fun startSiren() {
        if (mediaPlayer?.isPlaying == true) return

        try {
            // 1. Mevcut ses seviyesini kaydet (Siren kapatıldığında eski ses seviyesine dönmek için)
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

            // 2. Alarm sesini cihazın %100 maksimum seviyesine getir
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)

            // 3. Cihazın varsayılan Alarm Sesini yakala
            var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            // 4. Medya oynatıcıyı kur ve durdurulana kadar döngüye (loop) al
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alarmUri!!)
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
            e.printStackTrace()
        }
    }

    fun stopSiren() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            // Sesi kullanıcının eski ses seviyesine geri getir
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
