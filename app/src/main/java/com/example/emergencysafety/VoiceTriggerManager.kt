package com.example.emergencysafety

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    private var redCodeCount = 0
    private var deactivateCount = 0

    // Tetikleyici ve İptal Şifreleri (Küçük harf duyarlı)
    private val targetActivateWord = "kırmızı 41"
    private val targetDeactivateWord = "mavi 41"

    fun startListening() {
        if (isListening) return

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
            listenInternal()
        }
    }

    private fun listenInternal() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        isListening = true
        speechRecognizer?.startListening(intent)
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            // Sessizlik veya zamanaşımı hatalarında dinleme döngüsünü kesintisiz yeniden başlatır
            if (isListening) {
                listenInternal()
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let { processSpokenText(it) }

            // Döngünün sürekli canlı kalmasını sağlar
            if (isListening) {
                listenInternal()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun processSpokenText(matches: ArrayList<String>) {
        for (text in matches) {
            val lower = text.lowercase(Locale.getDefault())

            // 1. Kırmızı Kod Tetikleme Kontrolü
            if (lower.contains(targetActivateWord)) {
                redCodeCount++
                deactivateCount = 0 // Diğer sayacı sıfırla
                if (redCodeCount >= 2) {
                    redCodeCount = 0
                    onRedCodeTriggered()
                }
                return
            } 
            // 2. Kurtarma/İptal Kodu Kontrolü
            else if (lower.contains(targetDeactivateWord)) {
                deactivateCount++
                redCodeCount = 0 // Diğer sayacı sıfırla
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
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

