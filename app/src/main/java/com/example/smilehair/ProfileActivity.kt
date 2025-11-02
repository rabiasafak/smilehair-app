package com.example.smilehair

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Views
    private lateinit var ivHairPhoto: ImageView
    private lateinit var ivBeardPhoto: ImageView
    private lateinit var ivMustachePhoto: ImageView
    private lateinit var ivProfileImage: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etDateOfBirth: TextInputEditText
    private lateinit var etBloodType: TextInputEditText
    private lateinit var etAllergies: TextInputEditText
    private lateinit var spinnerGender: Spinner
    private lateinit var btnBack: ImageButton
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnSaveProfile: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnUploadHair: MaterialButton
    private lateinit var btnUploadBeard: MaterialButton
    private lateinit var btnUploadMustache: MaterialButton

    // RecyclerViews
    private lateinit var recyclerViewActiveTreatments: RecyclerView
    private lateinit var recyclerViewCompletedTreatments: RecyclerView
    private lateinit var tvNoActiveTreatments: TextView
    private lateinit var tvNoCompletedTreatments: TextView

    private var selectedPhotoType: String? = null
    private val PICK_IMAGE_REQUEST = 1001
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        initializeViews()
        setupGenderSpinner()
        setupClickListeners()
        loadUserProfile()
        loadTreatments()
    }

    private fun initializeViews() {
        ivHairPhoto = findViewById(R.id.ivHairPhoto)
        ivBeardPhoto = findViewById(R.id.ivBeardPhoto)
        ivMustachePhoto = findViewById(R.id.ivMustachePhoto)
        ivProfileImage = findViewById(R.id.profileImage)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etDateOfBirth = findViewById(R.id.etDateOfBirth)
        etBloodType = findViewById(R.id.etBloodType)
        etAllergies = findViewById(R.id.etAllergies)
        spinnerGender = findViewById(R.id.spinnerGender)
        btnBack = findViewById(R.id.btnBack)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnLogout = findViewById(R.id.btnLogout)
        btnUploadHair = findViewById(R.id.btnUploadHair)
        btnUploadBeard = findViewById(R.id.btnUploadBeard)
        btnUploadMustache = findViewById(R.id.btnUploadMustache)
        recyclerViewActiveTreatments = findViewById(R.id.recyclerViewActiveTreatments)
        recyclerViewCompletedTreatments = findViewById(R.id.recyclerViewCompletedTreatments)
        tvNoActiveTreatments = findViewById(R.id.tvNoActiveTreatments)
        tvNoCompletedTreatments = findViewById(R.id.tvNoCompletedTreatments)

        recyclerViewActiveTreatments.layoutManager = LinearLayoutManager(this)
        recyclerViewCompletedTreatments.layoutManager = LinearLayoutManager(this)
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Seçiniz", "Erkek", "Kadın", "Belirtmek İstemiyorum")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { goToMainActivity() }
        btnEditProfile.setOnClickListener { enableEditMode(true) }
        btnSaveProfile.setOnClickListener { saveProfile() }
        btnLogout.setOnClickListener { showLogoutDialog() }

        ivProfileImage.setOnClickListener { if (isEditMode) openGallery("profilePhoto") }
        ivHairPhoto.setOnClickListener { if (isEditMode) openGallery("hairFront") }
        ivBeardPhoto.setOnClickListener { if (isEditMode) openGallery("beardFront") }
        ivMustachePhoto.setOnClickListener { if (isEditMode) openGallery("mustacheFront") }

        btnUploadHair.setOnClickListener { openGallery("hairFront") }
        btnUploadBeard.setOnClickListener { openGallery("beardFront") }
        btnUploadMustache.setOnClickListener { openGallery("mustacheFront") }

        etDateOfBirth.setOnClickListener { if (isEditMode) showDatePicker() }
    }

    private fun enableEditMode(enable: Boolean) {
        isEditMode = enable

        etPhoneNumber.isEnabled = enable
        etDateOfBirth.isEnabled = enable
        etBloodType.isEnabled = enable
        etAllergies.isEnabled = enable
        spinnerGender.isEnabled = enable

        btnUploadHair.isEnabled = enable
        btnUploadBeard.isEnabled = enable
        btnUploadMustache.isEnabled = enable

        btnEditProfile.visibility = if (enable) View.GONE else View.VISIBLE
        btnSaveProfile.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDate = etDateOfBirth.text.toString()
        if (currentDate.isNotEmpty() && currentDate != "Doğum tarihi yok") {
            try {
                val date = dateFormat.parse(currentDate)
                date?.let { calendar.time = it }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Tarih parse hatası: ${e.message}")
            }
        }

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                etDateOfBirth.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveProfile() {
        val user = auth.currentUser ?: return
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val dateOfBirth = etDateOfBirth.text.toString().trim()
        val bloodType = etBloodType.text.toString().trim()
        val allergiesInput = etAllergies.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()

        val updates = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) updates["phoneNumber"] = phoneNumber
        if (bloodType.isNotEmpty()) updates["bloodType"] = bloodType
        if (gender != "Seçiniz") updates["gender"] = gender

        // Allergies artık array olarak kaydediliyor
        if (allergiesInput.isNotEmpty()) {
            val allergiesArray = allergiesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            updates["allergies"] = allergiesArray
        }

        // Tarih Timestamp olarak kaydediliyor
        if (dateOfBirth.isNotEmpty() && dateOfBirth != "Doğum tarihi yok") {
            try {
                val parsedDate = dateFormat.parse(dateOfBirth)
                parsedDate?.let { updates["dateOfBirth"] = Timestamp(it) }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Tarih parse edilemedi: ${e.message}")
            }
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "Değişiklik yapılmadı", Toast.LENGTH_SHORT).show()
            enableEditMode(false)
            return
        }

        db.collection("users").document(user.uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                enableEditMode(false)
                loadUserProfile() // Profili yeniden yükle
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Güncelleme başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Profil güncellenemedi: ${e.message}", e)
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setPositiveButton("Evet") { _, _ -> performLogout() }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("ProfileActivity", "Google çıkış yapıldı")
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        goToMainActivity()
    }

    private fun openGallery(type: String) {
        selectedPhotoType = type
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data ?: return
            uploadPhoto(imageUri)
        }
    }

    private fun uploadPhoto(uri: Uri) {
        val user = auth.currentUser ?: return
        val photoType = selectedPhotoType ?: return

        Toast.makeText(this, "Fotoğraf yükleniyor...", Toast.LENGTH_SHORT).show()

        val path = "users/${user.uid}/$photoType.jpg"
        val ref = storage.reference.child(path)

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    db.collection("users").document(user.uid)
                        .update(photoType, downloadUri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Fotoğraf başarıyla yüklendi", Toast.LENGTH_SHORT).show()
                            loadUserProfile()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Fotoğraf kaydedilemedi", Toast.LENGTH_SHORT).show()
                            Log.e("ProfileActivity", "Fotoğraf güncellenemedi: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Fotoğraf yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Fotoğraf yüklenemedi: ${e.message}", e)
            }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return

        Log.d("ProfileActivity", "Profil yükleniyor - User ID: ${user.uid}")

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    Log.w("ProfileActivity", "Kullanıcı belgesi bulunamadı")
                    return@addOnSuccessListener
                }

                Log.d("ProfileActivity", "Profil verisi: ${snapshot.data}")

                // Temel bilgiler
                tvUserName.text = snapshot.getString("name") ?: user.displayName ?: "Kullanıcı Adı"
                tvUserEmail.text = snapshot.getString("email") ?: user.email ?: "email@example.com"
                etPhoneNumber.setText(snapshot.getString("phoneNumber") ?: "")
                etBloodType.setText(snapshot.getString("bloodType") ?: "")

                // Tarih - Timestamp'ten Date'e çevirip formatla
                val dateOfBirth = snapshot.getTimestamp("dateOfBirth")?.toDate()
                etDateOfBirth.setText(dateOfBirth?.let { dateFormat.format(it) } ?: "")

                // Allergies array olarak alınıyor
                val allergiesList = snapshot.get("allergies") as? List<*>
                etAllergies.setText(allergiesList?.joinToString(", ") ?: "")

                // Cinsiyet
                val gender = snapshot.getString("gender") ?: "Seçiniz"
                val spinnerPosition = (spinnerGender.adapter as ArrayAdapter<String>).getPosition(gender)
                spinnerGender.setSelection(if (spinnerPosition >= 0) spinnerPosition else 0)

                // Profil fotoğrafı
                val profileUrl = snapshot.getString("profilePhoto")
                Log.d("ProfileActivity", "Profil foto URL: $profileUrl")
                Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(ivProfileImage)

                // Tedavi öncesi fotoğraflar
                val hairUrl = snapshot.getString("hairFront")
                Log.d("ProfileActivity", "Saç foto URL: $hairUrl")
                Glide.with(this)
                    .load(hairUrl)
                    .placeholder(R.drawable.ic_info)
                    .error(R.drawable.ic_info)
                    .into(ivHairPhoto)

                val beardUrl = snapshot.getString("beardFront")
                Log.d("ProfileActivity", "Sakal foto URL: $beardUrl")
                Glide.with(this)
                    .load(beardUrl)
                    .placeholder(R.drawable.ic_info)
                    .error(R.drawable.ic_info)
                    .into(ivBeardPhoto)

                val mustacheUrl = snapshot.getString("mustacheFront")
                Log.d("ProfileActivity", "Bıyık foto URL: $mustacheUrl")
                Glide.with(this)
                    .load(mustacheUrl)
                    .placeholder(R.drawable.ic_info)
                    .error(R.drawable.ic_info)
                    .into(ivMustachePhoto)

            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Profil yüklenemedi: ${e.message}", e)
                Toast.makeText(this, "Profil yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTreatments() {
        val user = auth.currentUser ?: return

        Log.d("ProfileActivity", "Tedaviler yükleniyor - User ID: ${user.uid}")

        // Aktif tedaviler
        db.collection("treatments")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("ProfileActivity", "Aktif tedavi sayısı: ${documents.size()}")

                if (documents.isEmpty) {
                    tvNoActiveTreatments.visibility = View.VISIBLE
                    recyclerViewActiveTreatments.visibility = View.GONE
                } else {
                    val activeTreatments = documents.mapNotNull {
                        it.toObject(Treatment::class.java).also { treatment ->
                            Log.d("ProfileActivity", "Aktif tedavi: ${treatment.getTreatmentTypeName()}")
                        }
                    }

                    tvNoActiveTreatments.visibility = View.GONE
                    recyclerViewActiveTreatments.visibility = View.VISIBLE

                    val adapter = TreatmentAdapter(activeTreatments) { treatment ->
                        openTreatmentDetail(treatment)
                    }
                    recyclerViewActiveTreatments.adapter = adapter
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Aktif tedaviler yüklenemedi: ${e.message}", e)
                tvNoActiveTreatments.visibility = View.VISIBLE
                recyclerViewActiveTreatments.visibility = View.GONE
            }

        // Tamamlanan tedaviler
        db.collection("treatments")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("ProfileActivity", "Tamamlanan tedavi sayısı: ${documents.size()}")

                if (documents.isEmpty) {
                    tvNoCompletedTreatments.visibility = View.VISIBLE
                    recyclerViewCompletedTreatments.visibility = View.GONE
                } else {
                    val completedTreatments = documents.mapNotNull {
                        it.toObject(Treatment::class.java).also { treatment ->
                            Log.d("ProfileActivity", "Tamamlanan tedavi: ${treatment.getTreatmentTypeName()}")
                        }
                    }

                    tvNoCompletedTreatments.visibility = View.GONE
                    recyclerViewCompletedTreatments.visibility = View.VISIBLE

                    val adapter = TreatmentAdapter(completedTreatments) { treatment ->
                        openTreatmentDetail(treatment)
                    }
                    recyclerViewCompletedTreatments.adapter = adapter
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Tamamlanan tedaviler yüklenemedi: ${e.message}", e)
                tvNoCompletedTreatments.visibility = View.VISIBLE
                recyclerViewCompletedTreatments.visibility = View.GONE
            }
    }

    private fun openTreatmentDetail(treatment: Treatment) {
        val intent = Intent(this, TreatmentDetailActivity::class.java)
        intent.putExtra("treatmentId", treatment.treatmentId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Sayfa her görünür olduğunda tedavileri yeniden yükle
        loadTreatments()
    }
}