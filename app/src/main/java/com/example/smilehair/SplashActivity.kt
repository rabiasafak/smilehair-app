package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // 2 saniye sonra yönlendirme
        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Kullanıcı zaten giriş yapmış → MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Giriş yapmamış → LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000) // 2 saniye splash
    }
}
