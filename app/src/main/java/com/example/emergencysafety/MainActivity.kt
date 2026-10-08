package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tasarımdaki gerçek buton ID'si (btnRedAlert) ile eşleştirildi
        val btnRedAlertId = resources.getIdentifier("btnRedAlert", "id", packageName)
        if (btnRedAlertId != 0) {
            val btnRedAlert = findViewById<View>(btnRedAlertId)
            btnRedAlert?.setOnClickListener {
                val intent = Intent(this, EmergencyOptionsActivity::class.java)
                startActivity(intent)
                // finish() YOK: Geri basılınca bu ekrana dönülecek.
            }
        }
    }
}
