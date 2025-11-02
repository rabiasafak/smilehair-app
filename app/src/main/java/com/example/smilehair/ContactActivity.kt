package com.example.smilehair

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        // Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // WhatsApp Card
        val whatsappCard = findViewById<MaterialCardView>(R.id.whatsappCard)
        whatsappCard.setOnClickListener {
            openWhatsApp()
        }

        // WhatsApp Button
        val whatsappButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.whatsappButton)
        whatsappButton.setOnClickListener {
            openWhatsApp()
        }

        // Floating WhatsApp Button
        val floatingWhatsapp = findViewById<FloatingActionButton>(R.id.floatingWhatsapp)
        floatingWhatsapp.setOnClickListener {
            openWhatsApp()
        }

        // Phone Card
        val phoneCard = findViewById<MaterialCardView>(R.id.phoneCard)
        phoneCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+905551234567")
            startActivity(intent)
        }

        // Email Card
        val emailCard = findViewById<MaterialCardView>(R.id.emailCard)
        emailCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:info@smilehairclinic.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "SmileHair İletişim")
            startActivity(Intent.createChooser(intent, "E-posta Gönder"))
        }

        // Address Card
        val addressCard = findViewById<MaterialCardView>(R.id.addressCard)
        addressCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("geo:0,0?q=Şişli, İstanbul, Türkiye")
            startActivity(intent)
        }

        // Instagram Card
        val instagramCard = findViewById<MaterialCardView>(R.id.instagramCard)
        instagramCard.setOnClickListener {
            openInstagram()
        }
    }

    private fun openWhatsApp() {
        try {
            val phoneNumber = "+905491492400" // WhatsApp numaranızı buraya yazın
            val message = "Merhaba, SmileHair hakkında bilgi almak istiyorum."
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openInstagram() {
        try {
            val username = "smilehair" // Instagram kullanıcı adınız
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        } catch (e: Exception) {
            // Instagram uygulaması yüklü değilse tarayıcıda aç
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/smilehairclinic/")
            startActivity(intent)
        }
    }
}