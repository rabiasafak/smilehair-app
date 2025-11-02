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

class KadinSacActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_kadin_sac)

        // HTML destekli metni göster
        val textView = findViewById<TextView>(R.id.kadinlardaSacKaybiNedenleri)
        textView.text = Html.fromHtml(
            getString(R.string.favori_ekimi),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView2 = findViewById<TextView>(R.id.kadinlardaSacKaybininSiniflandirilmasi)
        textView2.text = Html.fromHtml(
            getString(R.string.favori_ekimi_yan_etkiler),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView3 = findViewById<TextView>(R.id.kadinlardaSacKaybininTeshisi)
        textView3.text = Html.fromHtml(
            getString(R.string.favori_ekimi_sonrasi),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView4 = findViewById<TextView>(R.id.kadinlardaTedaviYontemleri)
        textView4.text = Html.fromHtml(
            getString(R.string.favori_dokulme_nedenleri),
            Html.FROM_HTML_MODE_LEGACY
        )
        val textView5 = findViewById<TextView>(R.id.kadinlardaBasari)
        textView5.text = Html.fromHtml(
            getString(R.string.favori_ekimi_oncesi),
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