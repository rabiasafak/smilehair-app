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
import com.google.firebase.firestore.Query

class SocialResponsibilityActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var projectsContainer: LinearLayout
    private lateinit var tvProjectCount: TextView

    // Proje 1
    private lateinit var cardProject1: MaterialCardView
    private lateinit var ivProject1: ImageView
    private lateinit var tvProject1Title: TextView
    private lateinit var tvProject1Description: TextView

    // Proje 2
    private lateinit var cardProject2: MaterialCardView
    private lateinit var ivProject2: ImageView
    private lateinit var tvProject2Title: TextView
    private lateinit var tvProject2Description: TextView

    private val db = FirebaseFirestore.getInstance()
    private val projectsList = mutableListOf<SocialResponsibility>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_responsibility)

        initViews()
        setupClickListeners()

        // İLK KULLANIM: Örnek verileri Firebase'e ekle (sadece bir kere çalıştır)
        //addSampleProjectsToFirebase()

        loadProjectsFromFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        projectsContainer = findViewById(R.id.projectsContainer)
        tvProjectCount = findViewById(R.id.tvProjectCount)

        // Proje 1
        cardProject1 = findViewById(R.id.cardProject1)
        ivProject1 = findViewById(R.id.ivProject1)
        tvProject1Title = findViewById(R.id.tvProject1Title)
        tvProject1Description = findViewById(R.id.tvProject1Description)

        // Proje 2
        cardProject2 = findViewById(R.id.cardProject2)
        ivProject2 = findViewById(R.id.ivProject2)
        tvProject2Title = findViewById(R.id.tvProject2Title)
        tvProject2Description = findViewById(R.id.tvProject2Description)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProjectsFromFirebase() {
        showLoading(true)

        db.collection("social_responsibility")
            .orderBy("order", Query.Direction.ASCENDING)
            .limit(2) // Sadece 2 proje
            .get()
            .addOnSuccessListener { documents ->
                projectsList.clear()

                for (document in documents) {
                    try {
                        val project = SocialResponsibility(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: "",
                            imageUrl = document.getString("imageUrl") ?: "",
                            order = document.getLong("order")?.toInt() ?: 0
                        )
                        projectsList.add(project)
                    } catch (e: Exception) {
                        continue
                    }
                }

                displayProjects()
                updateProjectCount(projectsList.size)
                showLoading(false)
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showContent(false)
                Toast.makeText(
                    this,
                    "Projeler yüklenirken hata oluştu: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayProjects() {
        if (projectsList.isEmpty()) {
            showContent(false)
            return
        }

        showContent(true)

        // Proje 1
        if (projectsList.size >= 1) {
            val project1 = projectsList[0]
            cardProject1.visibility = View.VISIBLE
            tvProject1Title.text = project1.title
            tvProject1Description.text = project1.description

            Glide.with(this)
                .load(project1.imageUrl)
                .placeholder(R.drawable.ic_info)
                .error(R.drawable.ic_info)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivProject1)
        }

        // Proje 2
        if (projectsList.size >= 2) {
            val project2 = projectsList[1]
            cardProject2.visibility = View.VISIBLE
            tvProject2Title.text = project2.title
            tvProject2Description.text = project2.description

            Glide.with(this)
                .load(project2.imageUrl)
                .placeholder(R.drawable.ic_info)
                .error(R.drawable.ic_info)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivProject2)
        }
    }

    private fun updateProjectCount(count: Int) {
        tvProjectCount.text = when (count) {
            0 -> "Topluma katkı projelerimiz"
            1 -> "1 sosyal sorumluluk projemiz"
            else -> "$count sosyal sorumluluk projemiz"
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        projectsContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showContent(hasContent: Boolean) {
        if (hasContent) {
            projectsContainer.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        } else {
            projectsContainer.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        }
    }

    // ========== ÖRNEK VERİLERİ EKLE (BİR KEZ ÇALIŞTIR) ==========
    private fun addSampleProjectsToFirebase() {
        val projects = listOf(
            hashMapOf(
                "title" to "Eğitime Destek Projesi",
                "description" to "Smile Hair Clinic olarak, eğitimin geleçeğimizi şekillendiren en önemli unsur olduğuna inanıyoruz. Bu kapsamda, maddi imkanları kısıtlı olan öğrencilere burs desteği sağlıyor, okul ve kütüphane yapımına katkıda bulunuyoruz. Ayrıca, kırsal bölgelerdeki okullara kitap ve kırtasiye yardımı yaparak, her çocuğun kaliteli eğitime erişimini destekliyoruz. Geleceğin parlak beyinlerinin önünü açmak için elimizden geleni yapıyoruz.",
                "imageUrl" to "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=800",
                "order" to 1
            ),
            hashMapOf(
                "title" to "Sağlık İçin Bir Arada Projesi",
                "description" to "Toplum sağlığının öneminin bilinciyle, düzenli olarak ücretsiz saç sağlığı taramaları ve bilgilendirme seminerleri düzenliyoruz. Hastaneler ve sağlık kuruluşlarıyla işbirliği yaparak, ihtiyaç sahibi hastalara ücretsiz saç ekimi ve tedavi desteği sunuyoruz. Ayrıca, kanser tedavisi gören hastalara peruk bağışı yaparak, onların bu zorlu süreçte yanlarında olduğumuzu gösteriyoruz. Sağlıklı bir toplum için elimizden geleni yapmaya devam ediyoruz.",
                "imageUrl" to "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800",
                "order" to 2
            )
        )

        projects.forEach { project ->
            db.collection("social_responsibility")
                .add(project)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Proje eklendi: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}