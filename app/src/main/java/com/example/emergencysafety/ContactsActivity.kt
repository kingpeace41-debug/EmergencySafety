package com.example.emergencysafety

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactsActivity : AppCompatActivity() {

    private lateinit var prefs: EmergencyPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        prefs = EmergencyPreferences(this)

        val etContact1Name = findViewById<EditText>(R.id.etContact1Name)
        val etContact1Number = findViewById<EditText>(R.id.etContact1Number)
        val etContact2Name = findViewById<EditText>(R.id.etContact2Name)
        val etContact2Number = findViewById<EditText>(R.id.etContact2Number)
        val etContact3Name = findViewById<EditText>(R.id.etContact3Name)
        val etContact3Number = findViewById<EditText>(R.id.etContact3Number)
        val etSharedMessage = findViewById<EditText>(R.id.etSharedMessage)
        val btnSave = findViewById<Button>(R.id.btnSaveContacts)

        // Var olan kayıtları getir
        etContact1Name.setText(prefs.contact1Name)
        etContact1Number.setText(prefs.contact1Number)
        etContact2Name.setText(prefs.contact2Name)
        etContact2Number.setText(prefs.contact2Number)
        etContact3Name.setText(prefs.contact3Name)
        etContact3Number.setText(prefs.contact3Number)
        etSharedMessage.setText(prefs.sharedMessage)

        btnSave.setOnClickListener {
            val c1Name = etContact1Name.text.toString().trim()
            val c1Num = etContact1Number.text.toString().trim()
            val c2Name = etContact2Name.text.toString().trim()
            val c2Num = etContact2Number.text.toString().trim()
            val c3Name = etContact3Name.text.toString().trim()
            val c3Num = etContact3Number.text.toString().trim()
            val message = etSharedMessage.text.toString().trim()

            if (c1Num.isEmpty() && c2Num.isEmpty() && c3Num.isEmpty()) {
                Toast.makeText(this, "En az 1 acil durum telefon numarası girmelisiniz!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                Toast.makeText(this, "Lütfen ortak acil durum mesajını boş bırakmayın!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.contact1Name = c1Name
            prefs.contact1Number = c1Num
            prefs.contact2Name = c2Name
            prefs.contact2Number = c2Num
            prefs.contact3Name = c3Name
            prefs.contact3Number = c3Num
            prefs.sharedMessage = message

            Toast.makeText(this, "İletişim bilgileri ve mesaj kaydedildi! ✅", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
