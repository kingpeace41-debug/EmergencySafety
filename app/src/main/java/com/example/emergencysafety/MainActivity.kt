package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnVoiceCodes)?.setOnClickListener {
            startActivity(Intent(this, VoiceCodesActivity::class.java))
        }

        findViewById<Button>(R.id.btnContacts)?.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        findViewById<Button>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnFakeDead)?.setOnClickListener {
            startActivity(Intent(this, FakeDeadActivity::class.java))
        }
    }
}
