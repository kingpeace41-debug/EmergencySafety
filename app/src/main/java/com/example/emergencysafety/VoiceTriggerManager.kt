package com.example.emergencysafety

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceTriggerManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private var onTriggerCallback: (() -> Unit)? = null
    private var isListening = false

    fun startListening(onRedCodeTriggered: () -> Unit) {
        this.onTriggerCallback = onRedCodeTriggered
        isListening = true

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e("VoiceTriggerManager", "Ses tanıma bu cihazda desteklenmiyor.")
            return
        }

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR") // Türkçe ses tanıma
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer?.startListening(recognizerIntent)
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            // Sessizlik veya hata durumunda dinlemeyi koparmadan tekrar başlatır
            if (isListening) {
                speechRecognizer?.startListening(recognizerIntent)
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches != null) {
                for (text in matches) {
                    val lowerText = text.lowercase(Locale.getDefault())
                    // Tetikleyici kelimeler (ihtiyacınıza göre kelime ekleyip çıkarabilirsiniz)
                    if (lowerText.contains("kırmızı kod") || lowerText.contains("yardım") || lowerText.contains("siren")) {
                        onTriggerCallback?.invoke()
                        break
                    }
                }
            }
            // Dinlemeye kesintisiz devam eder
            if (isListening) {
                speechRecognizer?.startListening(recognizerIntent)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
