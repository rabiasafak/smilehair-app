package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class HakkindaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hakkinda)

        // Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Menu items
        findViewById<MaterialCardView>(R.id.menuDoctors).setOnClickListener {
            startActivity(Intent(this, DoctorsActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuPress).setOnClickListener {
            startActivity(Intent(this, PressActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuEthics).setOnClickListener {
            startActivity(Intent(this, EthicsActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuSocialResponsibility).setOnClickListener {
            startActivity(Intent(this, SocialResponsibilityActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuPhilosophy).setOnClickListener {
            startActivity(Intent(this, PhilosophyActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuGentleCare).setOnClickListener {
            startActivity(Intent(this, GentleCareActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuWhyUs).setOnClickListener {
            startActivity(Intent(this, WhyUsActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.menuContact).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
    }
}
