package com.example.emergencysafety

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOpenMenu: View? = findViewById(R.id.btnOpenMenu)
        btnOpenMenu?.setOnClickListener {
            startActivity(Intent(this, InnerMenuActivity::class.java))
        }
    }
}
