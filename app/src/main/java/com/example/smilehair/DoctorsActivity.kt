package com.example.smilehair

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DoctorsActivity : AppCompatActivity() {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvDoctorCount: TextView
    private lateinit var btnBack: ImageButton

    private lateinit var doctorAdapter: DoctorDetailedAdapter
    private val doctorList = mutableListOf<Doctor>()
    private val filteredList = mutableListOf<Doctor>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors)

        initViews()
        setupRecyclerView()
        setupSearchFilter()
        setupClickListeners()
         fun addSampleDoctorsToFirebase() {
            val doctors = listOf(
                hashMapOf(
                    "name" to "Dr. Ahmet Yılmaz",
                    "specialty" to "Kardiyoloji",
                    "profileImageUrl" to "https://i.pravatar.cc/300?img=12",
                    "generalInfo" to "15 yıllık deneyime sahip kardiyoloji uzmanı. Hasta odaklı yaklaşımıyla tanınan Dr. Yılmaz, koroner arter hastalıkları konusunda uzmanlaşmıştır.",
                    "coreValues" to "Güven, empati ve bilimsel yaklaşım benim temel değerlerimdir. Her hastanın benzersiz olduğuna inanıyorum.",
                    "workStyle" to "Multidisipliner ekip çalışmasına önem veririm. Her hastaya özel tedavi planları oluştururum.",
                    "zodiacSign" to "Yengeç",
                    "favoritePerfume" to "Dior Sauvage",
                    "inspiringCities" to listOf("İstanbul", "Paris", "Tokyo")
                ),
                hashMapOf(
                    "name" to "Dr. Ayşe Demir",
                    "specialty" to "Nöroloji",
                    "profileImageUrl" to "https://i.pravatar.cc/300?img=45",
                    "generalInfo" to "Nöroloji alanında 12 yıldır hizmet vermekteyim. Migren, epilepsi ve hareket bozuklukları konularında uzmanım.",
                    "coreValues" to "Hastalarımın yaşam kalitesini artırmak ve onlara umut vermek en büyük motivasyonum.",
                    "workStyle" to "Kanıta dayalı tıp prensipleriyle çalışırım. Hastalarımı ailem gibi görürüm.",
                    "zodiacSign" to "Aslan",
                    "favoritePerfume" to "Chanel No. 5",
                    "inspiringCities" to listOf("Roma", "Barcelona", "New York")
                ),
                hashMapOf(
                    "name" to "Dr. Mehmet Kaya",
                    "specialty" to "Dermatoloji",
                    "profileImageUrl" to "https://i.pravatar.cc/300?img=33",
                    "generalInfo" to "Cilt sağlığı ve estetik dermatoloji alanında 10 yıllık deneyimim var. Lazer tedavileri ve cilt bakımı konusunda uzmanım.",
                    "coreValues" to "Doğal güzellik ve sağlıklı cilt benim önceliğimdir.",
                    "workStyle" to "Her hasta için kişiselleştirilmiş tedavi planları hazırlarım.",
                    "zodiacSign" to "İkizler",
                    "favoritePerfume" to "Tom Ford Oud Wood",
                    "inspiringCities" to listOf("Milano", "Londra", "Dubai")
                ),hashMapOf(
                    "name" to "Dr. Mehmet Kaya",
                    "specialty" to "Dermatoloji",
                    "profileImageUrl" to "https://i.pravatar.cc/300?img=33",
                    "generalInfo" to "Cilt sağlığı ve estetik dermatoloji alanında 10 yıllık deneyimim var. Lazer tedavileri ve cilt bakımı konusunda uzmanım.",
                    "coreValues" to "Doğal güzellik ve sağlıklı cilt benim önceliğimdir.",
                    "workStyle" to "Her hasta için kişiselleştirilmiş tedavi planları hazırlarım.",
                    "zodiacSign" to "İkizler",
                    "favoritePerfume" to "Tom Ford Oud Wood",
                    "inspiringCities" to listOf("Milano", "Londra", "Dubai")
                ),
                hashMapOf(
                    "name" to "Dr. Zeynep Aydın",
                    "specialty" to "Psikiyatri",
                    "profileImageUrl" to "https://i.pravatar.cc/300?img=47",
                    "generalInfo" to "Psikiyatri alanında 8 yıldır çalışıyorum. Anksiyete, depresyon ve uyku bozuklukları tedavisinde deneyimliyim.",
                    "coreValues" to "Empati ve gizlilik ilkelerine bağlıyım.",
                    "workStyle" to "Terapötik yaklaşımları ilaç tedavisiyle birleştirerek en iyi sonuçları almaya çalışırım.",
                    "zodiacSign" to "Balık",
                    "favoritePerfume" to "Jo Malone Wood Sage",
                    "inspiringCities" to listOf("Viyana", "Amsterdam", "Kyoto")
                )

            )

            doctors.forEach { doctor ->
                db.collection("doctors")
                    .add(doctor)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Doktor eklendi: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        loadDoctorsFromFirebase()
    }

    private fun initViews() {
        rvDoctors = findViewById(R.id.rvDoctors)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        etSearch = findViewById(R.id.etSearch)
        tvDoctorCount = findViewById(R.id.tvDoctorCount)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        doctorAdapter = DoctorDetailedAdapter(filteredList)
        rvDoctors.apply {
            layoutManager = LinearLayoutManager(this@DoctorsActivity)
            adapter = doctorAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchFilter() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadDoctorsFromFirebase() {
        showLoading(true)

        db.collection("doctors")
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                doctorList.clear()

                for (document in documents) {
                    try {
                        val doctor = Doctor(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            specialty = document.getString("specialty") ?: "",
                            profileImageUrl = document.getString("profileImageUrl"),
                            generalInfo = document.getString("generalInfo"),
                            coreValues = document.getString("coreValues"),
                            workStyle = document.getString("workStyle"),
                            zodiacSign = document.getString("zodiacSign"),
                            favoritePerfume = document.getString("favoritePerfume"),
                            inspiringCities = document.get("inspiringCities") as? List<String>
                        )
                        doctorList.add(doctor)
                    } catch (e: Exception) {
                        // Hatalı veri varsa atla
                        continue
                    }
                }

                filteredList.clear()
                filteredList.addAll(doctorList)
                doctorAdapter.notifyDataSetChanged()

                updateDoctorCount(doctorList.size)
                showLoading(false)
                updateEmptyState()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(
                    this,
                    "Doktorlar yüklenirken hata oluştu: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                updateEmptyState()
            }
    }

    private fun filterDoctors(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(doctorList)
        } else {
            val searchQuery = query.lowercase().trim()

            doctorList.forEach { doctor ->
                if (doctor.name.lowercase().contains(searchQuery) ||
                    doctor.specialty.lowercase().contains(searchQuery) ||
                    doctor.generalInfo?.lowercase()?.contains(searchQuery) == true ||
                    doctor.coreValues?.lowercase()?.contains(searchQuery) == true ||
                    doctor.workStyle?.lowercase()?.contains(searchQuery) == true
                ) {
                    filteredList.add(doctor)
                }
            }
        }

        doctorAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateDoctorCount(count: Int) {
        tvDoctorCount.text = if (count > 0) {
            "$count uzman doktorumuzla tanışın"
        } else {
            "Uzman doktorlarımızla tanışın"
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvDoctors.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun updateEmptyState() {
        if (filteredList.isEmpty() && !progressBar.isShown) {
            emptyState.visibility = View.VISIBLE
            rvDoctors.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvDoctors.visibility = View.VISIBLE
        }
    }
}