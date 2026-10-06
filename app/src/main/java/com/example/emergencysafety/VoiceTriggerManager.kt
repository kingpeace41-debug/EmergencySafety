package com.example.emergencysafety

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class VoiceTriggerManager(
    private val context: Context,
    private val onRedCodeTriggered: () -> Unit,
    private val onRedCodeDeactivated: () -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private val handler = Handler(Looper.getMainLooper())

    private var redCodeCount = 0
    private var deactivateCount = 0

    fun startListening() {
        if (isListening) return
        isListening = true

        handler.post {
            initAndStartSpeechRecognizer()
        }
    }

    private fun initAndStartSpeechRecognizer() {
        if (!isListening) return

        try {
            speechRecognizer?.destroy()
            speechRecognizer = null

            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(createRecognitionListener())

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                }

                speechRecognizer?.startListening(intent)
            }
        } catch (e: Exception) {
            restartListeningWithDelay()
        }
    }

    private fun restartListeningWithDelay() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isListening) {
                initAndStartSpeechRecognizer()
            }
        }, 300)
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            // Zamanaşımı veya ses kilitlenmesinde temizce yeniden başlat
            restartListeningWithDelay()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let { processSpokenText(it) }
            restartListeningWithDelay()
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun processSpokenText(matches: ArrayList<String>) {
        for (text in matches) {
            val lower = text.lowercase(Locale.getDefault())

            // 1. KIRMIZI KOD TETİKLEME ("Kırmızı 41")
            if (lower.contains("kırmızı") && (lower.contains("41") || lower.contains("kırk bir") || lower.contains("kırkbir"))) {
                redCodeCount++
                deactivateCount = 0
                if (redCodeCount >= 2) {
                    redCodeCount = 0
                    onRedCodeTriggered()
                }
                return
            } 
            // 2. KIRMIZI KOD İPTAL ("Kırmızı 42" veya "Mavi 41")
            else if (
                (lower.contains("kırmızı") && (lower.contains("42") || lower.contains("kırk iki") || lower.contains("kırkiki"))) ||
                (lower.contains("mavi") && (lower.contains("41") || lower.contains("kırk bir") || lower.contains("kırkbir")))
            ) {
                deactivateCount++
                redCodeCount = 0
                if (deactivateCount >= 2) {
                    deactivateCount = 0
                    onRedCodeDeactivated()
                }
                return
            }
        }
    }

    fun stopListening() {
        isListening = false
        handler.removeCallbacksAndMessages(null)
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        speechRecognizer = null
    }
}
