package com.example.smilehair

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class EthicsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var cardContent: MaterialCardView
    private lateinit var emptyState: LinearLayout
    private lateinit var tvEthicsTitle: TextView
    private lateinit var tvContentTitle: TextView
    private lateinit var tvContentBody: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ethics)

        initViews()
        setupClickListeners()

        // İLK KULLANIM: Örnek veriyi Firebase'e ekle (sadece bir kere çalıştır)
       // addSampleEthicsToFirebase()

        loadEthicsFromFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        cardContent = findViewById(R.id.cardContent)
        emptyState = findViewById(R.id.emptyState)
        tvEthicsTitle = findViewById(R.id.tvEthicsTitle)
        tvContentTitle = findViewById(R.id.tvContentTitle)
        tvContentBody = findViewById(R.id.tvContentBody)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadEthicsFromFirebase() {
        showLoading(true)

        // "ethics" koleksiyonundan "main" dökümanını çek
        db.collection("ethics")
            .document("main")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title") ?: "Etik İlkelerimiz"
                    val content = document.getString("content") ?: ""

                    tvEthicsTitle.text = title
                    tvContentTitle.text = title
                    tvContentBody.text = content

                    showContent(true)
                } else {
                    showContent(false)
                }
                showLoading(false)
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showContent(false)
                Toast.makeText(
                    this,
                    "İçerik yüklenirken hata oluştu: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        cardContent.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showContent(hasContent: Boolean) {
        if (hasContent) {
            cardContent.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        } else {
            cardContent.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        }
    }


    private fun addSampleEthicsToFirebase() {
        val ethics = hashMapOf(
            "title" to "Etik İlkelerimiz",
            "content" to """
                Smile Hair Clinic olarak, hasta sağlığı ve memnuniyeti bizim için her şeyden önce gelir. Etik değerlerimiz, hizmetlerimizin temelini oluşturur ve tüm çalışmalarımızda bu ilkelere bağlı kalırız.
                
                Dürüstlük ve Şeffaflık
                
                Hastalarımıza karşı her zaman dürüst ve şeffaf davranırız. Tedavi süreçleri, maliyetler ve beklenen sonuçlar hakkında eksiksiz bilgilendirme yaparız. Gerçekçi beklentiler oluşturur ve abartılı vaatlerde bulunmayız.
                
                Hasta Güvenliği
                
                Hasta güvenliği en öncelikli konumuzdur. Tüm işlemlerimizde uluslararası tıbbi standartlara ve hijyen kurallarına titizlikle uyarız. Modern teknoloji ve sterilizasyon yöntemleri kullanarak, hastalarımızın sağlığını koruruz.
                
                Profesyonellik
                
                Alanında uzman, deneyimli ve sürekli kendini geliştiren bir ekiple çalışırız. Tıbbi etik kurallara tam uyum gösterir, mesleki gelişimimizi sürdürürüz.
                
                Gizlilik
                
                Hastalarımızın kişisel ve tıbbi bilgilerini en üst düzeyde gizlilik ile koruruz. Bilgiler sadece tedavi amaçlı kullanılır ve üçüncü şahıslarla kesinlikle paylaşılmaz.
                
                Eşitlik ve Saygı
                
                Tüm hastalarımıza eşit davranır, din, dil, ırk, cinsiyet ayrımı yapmadan herkese aynı kalitede hizmet sunarız. Her bireyin değerli olduğuna inanır ve saygı ile yaklaşırız.
                
                Sürekli İyileştirme
                
                Hasta geri bildirimlerini değerlendirir, sürekli iyileştirme anlayışıyla hizmet kalitemizi artırırız. Yenilikçi yaklaşımlar benimser, sektördeki gelişmeleri takip ederiz.
                
                Bu etik ilkeler, Smile Hair Clinic'in temel değerleridir ve tüm çalışanlarımız bu prensipler doğrultusunda hareket eder.
            """.trimIndent()
        )

        db.collection("ethics")
            .document("main")
            .set(ethics)
            .addOnSuccessListener {
                Toast.makeText(this, "Etik içeriği eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}