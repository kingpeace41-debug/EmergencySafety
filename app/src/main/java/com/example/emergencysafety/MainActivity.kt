package com.example.yourapp // Kendi paket adınız ile değiştirin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // İzin isteme pencerelerinin sonuçlarını yakalayan launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isSmsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (isSmsGranted && (isFineLocationGranted || isCoarseLocationGranted)) {
            Toast.makeText(this, "Gerekli tüm izinler verildi.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Acil durum mesajının gönderilebilmesi için SMS ve Konum izinleri gereklidir.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Uygulama açılır açılmaz izinleri kontrol et ve iste
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // SMS izni verilmiş mi?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS)
        }

        // Hassas Konum izni verilmiş mi?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        
