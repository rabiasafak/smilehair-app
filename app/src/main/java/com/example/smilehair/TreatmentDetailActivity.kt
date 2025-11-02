package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class TreatmentDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tvTreatmentType: TextView
    private lateinit var tvDoctorName: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvTechnique: TextView
    private lateinit var tvGraftCount: TextView
    private lateinit var tvHairType: TextView
    private lateinit var tvNotes: TextView
    private lateinit var chipStatus: Chip
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var tvNoPhotos: TextView
    private lateinit var fabUploadPhoto: FloatingActionButton
    private lateinit var btnContactDoctor: MaterialButton

    private var treatmentId: String = ""
    private var treatment: Treatment? = null
    private val allPhotos = mutableListOf<TreatmentPhoto>()
    private val filteredPhotos = mutableListOf<TreatmentPhoto>()

    private var selectedAngle: String = "all" // "all", "front", "left", "right", "top", "back"
    private var selectedStage: String = "all" // "all", "before", "day1", etc.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treatment_detail)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        treatmentId = intent.getStringExtra("treatmentId") ?: ""

        if (treatmentId.isEmpty()) {
            Toast.makeText(this, "Tedavi ID bulunamadı", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        loadTreatmentDetails()
        loadPhotos()
        setupListeners()
    }

    private fun initViews() {
        tvTreatmentType = findViewById(R.id.tvTreatmentType)
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvTechnique = findViewById(R.id.tvTechnique)
        tvGraftCount = findViewById(R.id.tvGraftCount)
        tvHairType = findViewById(R.id.tvHairType)
        tvNotes = findViewById(R.id.tvNotes)
        chipStatus = findViewById(R.id.chipStatus)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerViewPhotos = findViewById(R.id.recyclerViewPhotos)
        tvNoPhotos = findViewById(R.id.tvNoPhotos)
        fabUploadPhoto = findViewById(R.id.fabUploadPhoto)
        btnContactDoctor = findViewById(R.id.btnContactDoctor)

        // RecyclerView setup - 2 sütun grid
        recyclerViewPhotos.layoutManager = GridLayoutManager(this, 2)

        // Tab layout setup
        setupTabs()
    }

    private fun setupTabs() {
        // Açı filtreleri
        tabLayout.addTab(tabLayout.newTab().setText("Tümü"))
        tabLayout.addTab(tabLayout.newTab().setText("Ön"))
        tabLayout.addTab(tabLayout.newTab().setText("Sol"))
        tabLayout.addTab(tabLayout.newTab().setText("Sağ"))
        tabLayout.addTab(tabLayout.newTab().setText("Üst"))
        tabLayout.addTab(tabLayout.newTab().setText("Arka"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedAngle = when (tab?.position) {
                    0 -> "all"
                    1 -> "front"
                    2 -> "left"
                    3 -> "right"
                    4 -> "top"
                    5 -> "back"
                    else -> "all"
                }
                filterPhotos()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        fabUploadPhoto.setOnClickListener {
            // Fotoğraf yükleme ekranına git
            val intent = Intent(this, PhotoUploadActivity::class.java)
            intent.putExtra("treatmentId", treatmentId)
            startActivity(intent)
        }

        btnContactDoctor.setOnClickListener {
            // WhatsApp ile doktora mesaj
            openWhatsApp()
        }

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun loadTreatmentDetails() {
        db.collection("treatments").document(treatmentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    treatment = document.toObject(Treatment::class.java)
                    displayTreatmentInfo()
                } else {
                    Toast.makeText(this, "Tedavi bulunamadı", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TreatmentDetail", "Error loading treatment", e)
                Toast.makeText(this, "Tedavi yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayTreatmentInfo() {
        treatment?.let { t ->
            tvTreatmentType.text = t.getTreatmentTypeName()
            tvDoctorName.text = "Dr. ${t.doctorName}"
            tvTechnique.text = "Teknik: ${t.technique}"
            tvGraftCount.text = "Greft Sayısı: ${t.graftCount}"
            tvHairType.text = "Saç Tipi: ${t.hairType}"
            tvNotes.text = if (t.notes.isNotEmpty()) t.notes else "Not yok"

            // Tarih
            t.startDate?.let { timestamp ->
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
                tvStartDate.text = "Başlangıç: ${sdf.format(timestamp.toDate())}"
            }

            // Durum
            chipStatus.text = t.getStatusText()
            chipStatus.setChipBackgroundColorResource(t.getStatusColor())
        }
    }

    private fun loadPhotos() {
        db.collection("treatments").document(treatmentId)
            .collection("photos")
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                allPhotos.clear()
                for (doc in documents) {
                    doc.toObject(TreatmentPhoto::class.java)?.let {
                        allPhotos.add(it)
                    }
                }
                filterPhotos()
            }
            .addOnFailureListener { e ->
                Log.e("TreatmentDetail", "Error loading photos", e)
                Toast.makeText(this, "Fotoğraflar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterPhotos() {
        filteredPhotos.clear()

        // Filtreleme
        filteredPhotos.addAll(allPhotos.filter { photo ->
            val angleMatch = selectedAngle == "all" || photo.angle == selectedAngle
            val stageMatch = selectedStage == "all" || photo.stage == selectedStage
            angleMatch && stageMatch
        })

        // UI güncelle
        if (filteredPhotos.isEmpty()) {
            tvNoPhotos.visibility = View.VISIBLE
            recyclerViewPhotos.visibility = View.GONE
        } else {
            tvNoPhotos.visibility = View.GONE
            recyclerViewPhotos.visibility = View.VISIBLE
            recyclerViewPhotos.adapter = TreatmentPhotoAdapter(filteredPhotos) { photo ->
                openPhotoFullscreen(photo)
            }
        }
    }

    private fun openPhotoFullscreen(photo: TreatmentPhoto) {
        // TODO: Fullscreen fotoğraf görüntüleme
        val intent = Intent(this, PhotoFullscreenActivity::class.java)
        intent.putExtra("imageUrl", photo.imageUrl)
        intent.putExtra("angle", photo.getAngleName())
        intent.putExtra("stage", photo.getStageName())
        startActivity(intent)
    }

    private fun openWhatsApp() {
        try {
            val phoneNumber = "+905491492400"
            val message = "Merhaba, ${treatment?.getTreatmentTypeName()} tedavim hakkında bilgi almak istiyorum."
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${android.net.Uri.encode(message)}")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp açılamadı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fotoğraf yükleme sonrası geri gelince yenile
        loadPhotos()
    }
}