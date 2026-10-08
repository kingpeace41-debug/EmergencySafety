package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEmergencyOptions = findViewById<Button>(R.id.btnEmergencyOptions)

        btnEmergencyOptions?.setOnClickListener {
            val intent = Intent(this, EmergencyOptionsActivity::class.java)
            startActivity(intent)
        }
    }
}
