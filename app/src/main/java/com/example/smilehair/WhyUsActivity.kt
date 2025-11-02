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

class WhyUsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var contentContainer: LinearLayout

    // Sebep 1
    private lateinit var cardReason1: MaterialCardView
    private lateinit var ivReason1: ImageView
    private lateinit var tvReason1Title: TextView
    private lateinit var tvReason1Description: TextView

    // Sebep 2
    private lateinit var cardReason2: MaterialCardView
    private lateinit var ivReason2: ImageView
    private lateinit var tvReason2Title: TextView
    private lateinit var tvReason2Description: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_why_us)

        initViews()
        setupClickListeners()

        // İLK KULLANIM: Örnek verileri Firebase'e ekle (sadece bir kere çalıştır)
        //addSampleWhyUsToFirebase()

        loadWhyUsFromFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        contentContainer = findViewById(R.id.contentContainer)

        // Sebep 1
        cardReason1 = findViewById(R.id.cardReason1)
        ivReason1 = findViewById(R.id.ivReason1)
        tvReason1Title = findViewById(R.id.tvReason1Title)
        tvReason1Description = findViewById(R.id.tvReason1Description)

        // Sebep 2
        cardReason2 = findViewById(R.id.cardReason2)
        ivReason2 = findViewById(R.id.ivReason2)
        tvReason2Title = findViewById(R.id.tvReason2Title)
        tvReason2Description = findViewById(R.id.tvReason2Description)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadWhyUsFromFirebase() {
        showLoading(true)

        // Sebep 1'i yükle
        db.collection("why_us")
            .document("reason1")
            .get()
            .addOnSuccessListener { doc1 ->
                var hasContent = false

                if (doc1.exists()) {
                    val title1 = doc1.getString("title")
                    val description1 = doc1.getString("description")
                    val imageUrl1 = doc1.getString("imageUrl")

                    if (!title1.isNullOrBlank() && !description1.isNullOrBlank()) {
                        cardReason1.visibility = View.VISIBLE
                        tvReason1Title.text = title1
                        tvReason1Description.text = description1

                        if (!imageUrl1.isNullOrBlank()) {
                            Glide.with(this)
                                .load(imageUrl1)
                                .placeholder(R.drawable.ic_info)
                                .error(R.drawable.ic_info)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .centerCrop()
                                .into(ivReason1)
                        }
                        hasContent = true
                    }
                }


                // Sebep 2'yi yükle
                db.collection("why_us")
                    .document("reason2")
                    .get()
                    .addOnSuccessListener { doc2 ->
                        if (doc2.exists()) {
                            val title2 = doc2.getString("title")
                            val description2 = doc2.getString("description")
                            val imageUrl2 = doc2.getString("imageUrl")

                            if (!title2.isNullOrBlank() && !description2.isNullOrBlank()) {
                                cardReason2.visibility = View.VISIBLE
                                tvReason2Title.text = title2
                                tvReason2Description.text = description2

                                if (!imageUrl2.isNullOrBlank()) {
                                    Glide.with(this)
                                        .load(imageUrl2)
                                        .placeholder(R.drawable.ic_info)
                                        .error(R.drawable.ic_info)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .centerCrop()
                                        .into(ivReason2)
                                }
                                hasContent = true
                            }
                        }

                        showContent(hasContent)
                        showLoading(false)
                    }
                    .addOnFailureListener { exception ->
                        showLoading(false)
                        showContent(hasContent)
                        Toast.makeText(
                            this,
                            "İçerik yüklenirken hata oluştu: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    // ========== ÖRNEK VERİLERİ EKLE (BİR KEZ ÇALIŞTIR) ==========
    private fun addSampleWhyUsToFirebase() {
        // Sebep 1
        val reason1 = hashMapOf(
            "title" to "Uzman Ekip ve Deneyim",
            "description" to """
                Smile Hair Clinic olarak, alanında uzman ve deneyimli doktorlardan oluşan bir ekibe sahibiz. Ekibimiz, saç ekimi konusunda yıllarca eğitim almış ve binlerce başarılı operasyon gerçekleştirmiştir.
                
                Her doktorumuz, uluslararası sertifikalara sahip olup, dünya standartlarında hizmet sunmaktadır. Sürekli eğitim ve gelişim programlarımız sayesinde, en son teknikleri ve yöntemleri uyguluyoruz.
                
                Hastalarımızın güvenliği ve memnuniyeti bizim önceliğimizdir. Bu nedenle, her operasyonda titizlikle çalışır ve en yüksek kalite standartlarını garanti ederiz. Deneyimimiz, size en iyi sonuçları sunmamızın temelidir.
            """.trimIndent(),
            "imageUrl" to "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=800"
        )

        // Sebep 2
        val reason2 = hashMapOf(
            "title" to "İleri Teknoloji ve Yenilikçi Yaklaşım",
            "description" to """
                Modern tıbbın sunduğu tüm imkanlardan faydalanarak, en son teknolojilerle donatılmış kliniklerimizde hizmet veriyoruz. DHI, FUE, Sapphire FUE gibi güncel saç ekimi tekniklerini başarıyla uyguluyoruz.
                
                Robotik saç ekimi sistemleri, 3D saç analizi cihazları ve dijital görüntüleme teknolojileri ile operasyon öncesi ve sonrası süreci en detaylı şekilde takip ediyoruz. Bu teknolojiler sayesinde, daha doğal, daha hızlı ve daha az invaziv işlemler gerçekleştiriyoruz.
                
                Yenilikçi yaklaşımımız, sadece teknolojiyle sınırlı değil. Hasta deneyimini iyileştirmek, konfor ve güvenliği artırmak için sürekli yeni yöntemler geliştiriyor ve uyguluyoruz. Geleceğin tıbbını bugünden sunuyoruz.
            """.trimIndent(),
            "imageUrl" to "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=800"
        )

        // Firebase'e kaydet
        db.collection("why_us")
            .document("reason1")
            .set(reason1)
            .addOnSuccessListener {
                Toast.makeText(this, "Sebep 1 eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        db.collection("why_us")
            .document("reason2")
            .set(reason2)
            .addOnSuccessListener {
                Toast.makeText(this, "Sebep 2 eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}