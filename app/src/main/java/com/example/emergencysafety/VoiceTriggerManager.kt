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
    private var onRedCodeCallback: (() -> Unit)? = null
    private var onCancelCodeCallback: (() -> Unit)? = null
    private var isListening = false

    fun startListening(
        onRedCodeTriggered: () -> Unit,
        onCancelTriggered: () -> Unit
    ) {
        this.onRedCodeCallback = onRedCodeTriggered
        this.onCancelCodeCallback = onCancelTriggered
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
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
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
            if (isListening) {
                speechRecognizer?.startListening(recognizerIntent)
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches != null) {
                for (text in matches) {
                    val lowerText = text.lowercase(Locale("tr", "TR"))
                    
                    val hasFortyOne = lowerText.contains("41") || lowerText.contains("kırk bir")
                    val hasFortyTwo = lowerText.contains("42") || lowerText.contains("kırk iki")

                    // Alarmı Başlatan Kodlar
                    val isRed41 = lowerText.contains("kırmızı") && hasFortyOne
                    val isSiren41 = lowerText.contains("siren") && hasFortyOne

                    // Alarmı Kapatan Kodlar
                    val isBlue41 = lowerText.contains("mavi") && hasFortyOne
                    val isRed42 = lowerText.contains("kırmızı") && hasFortyTwo

                    if (isRed41 || isSiren41) {
                        onRedCodeCallback?.invoke()
                        break
                    } else if (isBlue41 || isRed42) {
                        onCancelCodeCallback?.invoke()
                        break
                    }
                }
            }
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
