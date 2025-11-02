package com.example.smilehair

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class GentleCareActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var cardContent: MaterialCardView
    private lateinit var cardImage: MaterialCardView
    private lateinit var ivGentleCare: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gentle_care)

        initViews()
        setupClickListeners()

        // İLK KULLANIM: Örnek veriyi Firebase'e ekle (sadece bir kere çalıştır)
        //addSampleGentleCareToFirebase()

        loadGentleCareFromFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        cardContent = findViewById(R.id.cardContent)
        cardImage = findViewById(R.id.cardImage)
        ivGentleCare = findViewById(R.id.ivGentleCare)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadGentleCareFromFirebase() {
        showLoading(true)

        db.collection("gentle_care")
            .document("main")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title") ?: "Gentle Care"
                    val description = document.getString("description") ?: ""
                    val imageUrl = document.getString("imageUrl")

                    tvTitle.text = title
                    tvDescription.text = description

                    // Görsel varsa yükle
                    if (!imageUrl.isNullOrBlank()) {
                        cardImage.visibility = View.VISIBLE
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_info)
                            .error(R.drawable.ic_info)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .centerCrop()
                            .into(ivGentleCare)
                    } else {
                        cardImage.visibility = View.GONE
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

    // ========== ÖRNEK VERİYİ EKLE (BİR KEZ ÇALIŞTIR) ==========
    private fun addSampleGentleCareToFirebase() {
        val gentleCare = hashMapOf(
            "title" to "Gentle Care - Nazik Bakım Yaklaşımımız",
            "description" to """
                Smile Hair Clinic olarak, "Gentle Care" felsefesi ile hastalarımıza en nazik ve özenli bakımı sunuyoruz. Saç ekimi sadece teknik bir işlem değil, aynı zamanda duygusal bir yolculuktur ve biz bu yolculukta hastalarımızın yanında olmayı taahhüt ediyoruz.
                
                Nazik Dokunuş
                
                Her işlemde nazik bir dokunuş kullanırız. Ağrısız ve konforlu bir deneyim sağlamak için en son anestezi teknikleri ve yöntemleri uygularız. Hastalarımızın rahatını ön planda tutarak, işlem sırasında minimum rahatsızlık hissetmelerini sağlarız.
                
                Kişiye Özel İlgi
                
                Her hastamız bizim için özeldir. İlk görüşmeden son kontrole kadar, kişiye özel ilgi ve destek sağlarız. Hastalarımızın endişelerini dinler, sorularını sabırla yanıtlar ve onları her adımda bilgilendiririz.
                
                Güvenli ve Steril Ortam
                
                Hasta güvenliği bizim önceliğimizdir. Kliniklerimiz en yüksek hijyen ve sterilizasyon standartlarına sahiptir. Tüm ekipmanlarımız tek kullanımlık veya tam sterilize edilmiş olup, hasta sağlığını tehlikeye atacak hiçbir unsura izin vermeyiz.
                
                Psikolojik Destek
                
                Saç kaybı ve saç ekimi süreci duygusal olarak zorlayıcı olabilir. Bu nedenle, sadece fiziksel değil, psikolojik destek de sağlarız. Hastalarımızın kendilerini değerli ve anlaşılmış hissetmelerini sağlamak için her türlü çabayı gösteririz.
                
                Sürekli Takip ve Destek
                
                İşlem sonrası bakım ve takip, başarının anahtarıdır. Hastalarımızla düzenli iletişim halinde kalır, iyileşme sürecinde rehberlik eder ve her türlü sorunlarında yanlarında oluruz. Gentle Care, sadece ameliyat masasında değil, tüm süreç boyunca devam eder.
                
                Smile Hair Clinic'te Gentle Care, sadece bir slogan değil, bir yaşam biçimidir. Her hastamıza, sevdiklerimize davranır gibi davranır ve onların en iyi deneyimi yaşamalarını sağlamak için elimizden geleni yaparız.
            """.trimIndent(),
            "imageUrl" to "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800"
        )

        db.collection("gentle_care")
            .document("main")
            .set(gentleCare)
            .addOnSuccessListener {
                Toast.makeText(this, "Gentle Care içeriği eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}