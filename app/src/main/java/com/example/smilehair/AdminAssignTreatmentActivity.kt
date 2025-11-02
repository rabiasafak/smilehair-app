package com.example.smilehair

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdminAssignTreatmentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tvUserName: TextView
    private lateinit var spinnerTreatmentType: Spinner
    private lateinit var spinnerTechnique: Spinner
    private lateinit var spinnerHairType: Spinner
    private lateinit var etDoctorName: TextInputEditText
    private lateinit var etClinicName: TextInputEditText
    private lateinit var etGraftCount: TextInputEditText
    private lateinit var etStartDate: TextInputEditText
    private lateinit var etNotes: TextInputEditText
    private lateinit var btnCreateTreatment: MaterialButton

    private var userId = ""
    private var selectedDate: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_assign_treatment)

        db = FirebaseFirestore.getInstance()

        // Intent'ten kullanıcı bilgilerini al
        userId = intent.getStringExtra("userId") ?: ""
        val userName = intent.getStringExtra("userName") ?: "Bilinmeyen"

        if (userId.isEmpty()) {
            Toast.makeText(this, "Kullanıcı ID bulunamadı", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        tvUserName.text = "Hasta: $userName"
        setupSpinners()
        setupListeners()
    }

    private fun initViews() {
        tvUserName = findViewById(R.id.tvUserName)
        spinnerTreatmentType = findViewById(R.id.spinnerTreatmentType)
        spinnerTechnique = findViewById(R.id.spinnerTechnique)
        spinnerHairType = findViewById(R.id.spinnerHairType)
        etDoctorName = findViewById(R.id.etDoctorName)
        etClinicName = findViewById(R.id.etClinicName)
        etGraftCount = findViewById(R.id.etGraftCount)
        etStartDate = findViewById(R.id.etStartDate)
        etNotes = findViewById(R.id.etNotes)
        btnCreateTreatment = findViewById(R.id.btnCreateTreatment)
    }

    private fun setupSpinners() {
        // Tedavi Tipleri
        val treatmentTypes = Treatment.getTreatmentTypes().map { it.second }
        val treatmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, treatmentTypes)
        treatmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTreatmentType.adapter = treatmentAdapter

        // Teknikler
        val techniques = Treatment.getTechniques()
        val techniqueAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, techniques)
        techniqueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTechnique.adapter = techniqueAdapter

        // Saç Tipleri
        val hairTypes = Treatment.getHairTypes()
        val hairAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hairTypes)
        hairAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHairType.adapter = hairAdapter
    }

    private fun setupListeners() {
        etStartDate.setOnClickListener {
            showDatePicker()
        }

        btnCreateTreatment.setOnClickListener {
            createTreatment()
        }

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Tedavi Başlangıç Tarihi")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = Timestamp(Date(selection))
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
            etStartDate.setText(sdf.format(Date(selection)))
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun createTreatment() {
        // Validasyon
        val doctorName = etDoctorName.text.toString().trim()
        if (doctorName.isEmpty()) {
            etDoctorName.error = "Doktor adı gerekli"
            return
        }

        val clinicName = etClinicName.text.toString().trim()
        if (clinicName.isEmpty()) {
            etClinicName.error = "Klinik adı gerekli"
            return
        }

        val graftCount = etGraftCount.text.toString().trim()
        if (graftCount.isEmpty()) {
            etGraftCount.error = "Greft sayısı gerekli"
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Lütfen başlangıç tarihi seçin", Toast.LENGTH_SHORT).show()
            return
        }

        // Tedavi objesi oluştur
        val treatmentId = db.collection("treatments").document().id
        val treatmentTypeIndex = spinnerTreatmentType.selectedItemPosition
        val treatmentTypeCode = Treatment.getTreatmentTypes()[treatmentTypeIndex].first

        val treatment = Treatment(
            treatmentId = treatmentId,
            userId = userId,
            treatmentType = treatmentTypeCode,
            doctorName = doctorName,
            clinicName = clinicName,
            technique = spinnerTechnique.selectedItem.toString(),
            graftCount = graftCount,
            hairType = spinnerHairType.selectedItem.toString(),
            status = "active",
            startDate = selectedDate,
            notes = etNotes.text.toString().trim(),
            createdAt = Timestamp.now()
        )

        // Firestore'a kaydet
        db.collection("treatments").document(treatmentId)
            .set(treatment.toMap())
            .addOnSuccessListener {
                Toast.makeText(this, "Tedavi başarıyla atandı!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("AdminAssign", "Error creating treatment", e)
                Toast.makeText(this, "Tedavi oluşturulamadı: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}