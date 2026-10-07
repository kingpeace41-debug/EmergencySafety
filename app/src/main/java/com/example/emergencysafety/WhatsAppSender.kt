package com.example.emergencysafety

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object WhatsAppSender {

    // META DEVELOPER PANELİNDEN ALINAN BİLGİLER
    private const val PHONE_NUMBER_ID = "15556447058"
    private const val ACCESS_TOKEN = "EAAzrYfZBaGKYBSpLG6x8WpAFtGznUvBeOFM4kPsrooeBqFjEAI2UFiuVupqVbUh60zZAZAEpJfdbh0UiY350exX4ZBXGi9rguAhiPXJX6TukAMtUJuJcKJ0nYK9HTskuX0bt8BnFl4rrFZCxSeKQV1uVBTdnkXAz7DUaE6V2uSOZCtC0aiUKGM0U25LFy6ZAp926ZCWYyDdFgxlR0oakomtZC5RkMkbdlmhTL5j8ieuRZCCkFSvfjxz5kZD"

    /**
     * Kayıtlı tüm kişilere ortak acil durum mesajını gönderir.
     */
    fun sendEmergencyMessageToAll(context: Context, locationUrl: String? = null) {
        val prefs = EmergencyPreferences(context)
        
        val contacts = listOfNotNull(
            prefs.contact1Number.takeIf { it.isNotBlank() },
            prefs.contact2Number.takeIf { it.isNotBlank() },
            prefs.contact3Number.takeIf { it.isNotBlank() }
        )

        if (contacts.isEmpty()) {
            Log.e("WhatsAppSender", "Gönderilecek kayıtlı numara bulunamadı!")
            return
        }

        var fullMessage = prefs.sharedMessage
        if (!locationUrl.isNullOrEmpty()) {
            fullMessage += "\n\n📍 Konum Bilgim:\n$locationUrl"
        }

        for (phoneNumber in contacts) {
            sendMessageViaCloudApi(phoneNumber, fullMessage)
        }
    }

    private fun sendMessageViaCloudApi(phoneNumber: String, textMessage: String) {
        thread {
            try {
                // Telefon numarasını temizle (+ ve boşlukları kaldır)
                val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").trim()
                val apiUrl = URL("https://graph.facebook.com/v18.0/$PHONE_NUMBER_ID/messages")

                val conn = apiUrl.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $ACCESS_TOKEN")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                // WhatsApp Cloud API JSON Gövdesi
                val jsonBody = JSONObject().apply {
                    put("messaging_product", "whatsapp")
                    put("to", cleanPhone)
                    put("type", "text")
                    put("text", JSONObject().put("body", textMessage))
                }

                val writer = OutputStreamWriter(conn.outputStream, "UTF-8")
                writer.write(jsonBody.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                Log.d("WhatsAppSender", "Mesaj Gönderildi ($cleanPhone) - HTTP Yanıt Kodu: $responseCode")

                conn.disconnect()
            } catch (e: Exception) {
                Log.e("WhatsAppSender", "WhatsApp mesajı gönderilirken hata oluştu", e)
            }
        }
    }
}
