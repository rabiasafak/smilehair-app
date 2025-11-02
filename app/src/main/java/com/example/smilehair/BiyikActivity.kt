package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.Html
import android.view.View
import android.widget.ImageButton

class BiyikActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_biyik)

        // HTML destekli metni göster
        val textView = findViewById<TextView>(R.id.biyikEkimiNedir)
        textView.text = Html.fromHtml(
            getString(R.string.biyik_ekimi_bilgi),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView2 = findViewById<TextView>(R.id.biyikEkimSonrasi)
        textView2.text = Html.fromHtml(
            getString(R.string.biyik_ekimi_oncesi_sonrasi),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView3 = findViewById<TextView>(R.id.biyikSiziNelerBekliyor)
        textView3.text = Html.fromHtml(
            getString(R.string.biyik_ekimi_iyilesme_sureci),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView4 = findViewById<TextView>(R.id.biyikDokulmesininNedenleri)
        textView4.text = Html.fromHtml(
            getString(R.string.biyik_dokulme_nedenleri),
            Html.FROM_HTML_MODE_LEGACY
        )
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Eski sayfa sağa kayar, yeni sayfa fade in olur
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
            finish()
        }


        // Window insets - root view'ı kullan
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}