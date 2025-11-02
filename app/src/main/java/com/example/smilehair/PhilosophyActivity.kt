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

class PhilosophyActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var contentContainer: LinearLayout

    // Ana içerik
    private lateinit var tvIntroduction: TextView

    // Bölüm 1
    private lateinit var cardSection1: MaterialCardView
    private lateinit var tvSection1Title: TextView
    private lateinit var tvSection1Content: TextView

    // Bölüm 2
    private lateinit var cardSection2: MaterialCardView
    private lateinit var tvSection2Title: TextView
    private lateinit var tvSection2Content: TextView

    // Bölüm 3
    private lateinit var cardSection3: MaterialCardView
    private lateinit var tvSection3Title: TextView
    private lateinit var tvSection3Content: TextView

    // Bölüm 4
    private lateinit var cardSection4: MaterialCardView
    private lateinit var tvSection4Title: TextView
    private lateinit var tvSection4Content: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_philosophy)

        initViews()
        setupClickListeners()

        // İLK KULLANIM: Örnek veriyi Firebase'e ekle (sadece bir kere çalıştır)
        //addSamplePhilosophyToFirebase()

        loadPhilosophyFromFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        contentContainer = findViewById(R.id.contentContainer)
        tvIntroduction = findViewById(R.id.tvIntroduction)

        // Bölüm 1
        cardSection1 = findViewById(R.id.cardSection1)
        tvSection1Title = findViewById(R.id.tvSection1Title)
        tvSection1Content = findViewById(R.id.tvSection1Content)

        // Bölüm 2
        cardSection2 = findViewById(R.id.cardSection2)
        tvSection2Title = findViewById(R.id.tvSection2Title)
        tvSection2Content = findViewById(R.id.tvSection2Content)

        // Bölüm 3
        cardSection3 = findViewById(R.id.cardSection3)
        tvSection3Title = findViewById(R.id.tvSection3Title)
        tvSection3Content = findViewById(R.id.tvSection3Content)

        // Bölüm 4
        cardSection4 = findViewById(R.id.cardSection4)
        tvSection4Title = findViewById(R.id.tvSection4Title)
        tvSection4Content = findViewById(R.id.tvSection4Content)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadPhilosophyFromFirebase() {
        showLoading(true)

        db.collection("philosophy")
            .document("main")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Giriş metni
                    val introduction = document.getString("introduction") ?: ""
                    tvIntroduction.text = introduction

                    // Bölüm 1
                    val section1Title = document.getString("section1_title")
                    val section1Content = document.getString("section1_content")
                    if (!section1Title.isNullOrBlank() && !section1Content.isNullOrBlank()) {
                        cardSection1.visibility = View.VISIBLE
                        tvSection1Title.text = section1Title
                        tvSection1Content.text = section1Content
                    }

                    // Bölüm 2
                    val section2Title = document.getString("section2_title")
                    val section2Content = document.getString("section2_content")
                    if (!section2Title.isNullOrBlank() && !section2Content.isNullOrBlank()) {
                        cardSection2.visibility = View.VISIBLE
                        tvSection2Title.text = section2Title
                        tvSection2Content.text = section2Content
                    }

                    // Bölüm 3
                    val section3Title = document.getString("section3_title")
                    val section3Content = document.getString("section3_content")
                    if (!section3Title.isNullOrBlank() && !section3Content.isNullOrBlank()) {
                        cardSection3.visibility = View.VISIBLE
                        tvSection3Title.text = section3Title
                        tvSection3Content.text = section3Content
                    }

                    // Bölüm 4
                    val section4Title = document.getString("section4_title")
                    val section4Content = document.getString("section4_content")
                    if (!section4Title.isNullOrBlank() && !section4Content.isNullOrBlank()) {
                        cardSection4.visibility = View.VISIBLE
                        tvSection4Title.text = section4Title
                        tvSection4Content.text = section4Content
                    }

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
        contentContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showContent(hasContent: Boolean) {
        if (hasContent) {
            contentContainer.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        } else {
            contentContainer.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        }
    }

    // ========== ÖRNEK VERİYİ EKLE (BİR KEZ ÇALIŞTIR) ==========
    private fun addSamplePhilosophyToFirebase() {
        val philosophy = hashMapOf(
            "introduction" to """
                Smile Hair Clinic olarak, sadece saç ekimi yapmıyoruz; insanların özgüvenini yeniden inşa ediyor, hayatlarına dokunuyoruz. Smile True Felsefemiz, bu anlayışın temelidir ve tüm çalışmalarımızı şekillendirir.
            """.trimIndent(),

            "section1_title" to "Gerçek Bir Gülümseme",
            "section1_content" to """
                Amacımız, hastalarımızın yüzünde gerçek bir gülümseme görmektir. Bu sadece estetik bir dönüşüm değil, aynı zamanda içsel bir değişimdir. Her hastamızın kendine olan güvenini artırmak, sosyal hayatında daha mutlu ve başarılı olmasını sağlamak için çalışırız. Smile True, sadece saç ekimi değil, hayata yeniden gülümseme felsefesidir.
            """.trimIndent(),

            "section2_title" to "Doğallık ve Kalite",
            "section2_content" to """
                Doğal görünüm bizim için vazgeçilmezdir. Yapay veya abartılı sonuçlar yerine, hastalarımızın kendi saç yapısına uygun, doğal ve estetik sonuçlar elde etmesini hedefleriz. En yüksek kalite standartlarında çalışır, her detaya özen gösteririz. Kullandığımız teknolojiler ve yöntemler, dünya standartlarında olup sürekli güncellenmektedir.
            """.trimIndent(),

            "section3_title" to "Hastaya Özel Yaklaşım",
            "section3_content" to """
                Her insan farklıdır ve bu nedenle her tedavi planı kişiye özeldir. Hastalarımızı dinler, beklentilerini anlar ve onlara en uygun çözümü sunarız. Şablon tedaviler yerine, bireye özel planlamalar yaparız. Bu yaklaşım, hem daha iyi sonuçlar elde etmemizi sağlar hem de hastalarımızın memnuniyetini artırır.
            """.trimIndent(),

            "section4_title" to "Sürekli Gelişim ve İnovasyon",
            "section4_content" to """
                Tıp dünyası sürekli gelişiyor ve biz de bu gelişmelerin en ön saflarında yer alıyoruz. Yeni teknolojileri takip eder, ekibimizi sürekli eğitir ve en iyi uygulamaları benimseriz. İnovasyon, sadece teknolojiyle sınırlı değildir; hizmet kalitemizi, hasta deneyimimizi ve süreçlerimizi de sürekli iyileştiririz. Smile True Felsefesi, durağanlığı değil, sürekli gelişimi temsil eder.
            """.trimIndent()
        )

        db.collection("philosophy")
            .document("main")
            .set(philosophy)
            .addOnSuccessListener {
                Toast.makeText(this, "Felsefe içeriği eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}