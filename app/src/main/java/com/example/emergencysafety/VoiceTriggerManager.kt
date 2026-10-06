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
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(createRecognitionListener())
                listenInternal()
            }
        }
    }

    private fun listenInternal() {
        if (!isListening) return
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            restartListeningWithDelay()
        }
    }

    private fun restartListeningWithDelay() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isListening) {
                listenInternal()
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

            // Kırmızı Kod Varyasyon Kontrolü ("kırmızı 41", "kırmızı kırk bir", "kırmızı kırkbir")
            if (lower.contains("kırmızı") && (lower.contains("41") || lower.contains("kırk bir") || lower.contains("kırkbir"))) {
                redCodeCount++
                deactivateCount = 0
                if (redCodeCount >= 2) {
                    redCodeCount = 0
                    onRedCodeTriggered()
                }
                return
            } 
            // Mavi Kod Varyasyon Kontrolü ("mavi 41", "mavi kırk bir", "mavi kırkbir")
            else if (lower.contains("mavi") && (lower.contains("41") || lower.contains("kırk bir") || lower.contains("kırkbir"))) {
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
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
