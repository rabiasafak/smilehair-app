package com.example.smilehair

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class PhotoUploadActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var ivPreview: ImageView
    private lateinit var chipGroupAngle: ChipGroup
    private lateinit var spinnerStage: Spinner
    private lateinit var etNotes: TextInputEditText
    private lateinit var btnSelectPhoto: MaterialButton
    private lateinit var btnTakePhoto: MaterialButton
    private lateinit var btnUpload: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var treatmentId: String = ""
    private var selectedImageUri: Uri? = null
    private var selectedImageBitmap: Bitmap? = null
    private var selectedAngle: String = ""
    private var selectedStage: String = ""

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_upload)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        treatmentId = intent.getStringExtra("treatmentId") ?: ""

        if (treatmentId.isEmpty()) {
            Toast.makeText(this, "Tedavi ID bulunamadı", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        ivPreview = findViewById(R.id.ivPreview)
        chipGroupAngle = findViewById(R.id.chipGroupAngle)
        spinnerStage = findViewById(R.id.spinnerStage)
        etNotes = findViewById(R.id.etNotes)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnUpload = findViewById(R.id.btnUpload)
        progressBar = findViewById(R.id.progressBar)

        val stages = TreatmentPhoto.getStageNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stages)
        spinnerStage.adapter = adapter
        btnUpload.isEnabled = false
    }

    private fun setupListeners() {
        btnSelectPhoto.setOnClickListener { openGallery() }
        btnTakePhoto.setOnClickListener { openCamera() }
        btnUpload.setOnClickListener {
            if (validateInputs()) uploadPhoto()
        }
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }

        chipGroupAngle.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = findViewById<Chip>(checkedIds[0])
                selectedAngle = when (selectedChip.text.toString()) {
                    "Ön" -> "front"
                    "Sol" -> "left"
                    "Sağ" -> "right"
                    "Üst" -> "top"
                    "Arka" -> "back"
                    else -> ""
                }
                checkUploadReady()
            }
        }

        spinnerStage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStage = when (position) {
                    0 -> "before"
                    1 -> "day1"
                    2 -> "week1"
                    3 -> "month1"
                    4 -> "month3"
                    5 -> "month6"
                    6 -> "month12"
                    else -> ""
                }
                checkUploadReady()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST)
        } else {
            Toast.makeText(this, "Kamera uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    selectedImageBitmap = null
                    ivPreview.setImageURI(selectedImageUri)
                    ivPreview.visibility = View.VISIBLE
                    checkUploadReady()
                }
                CAMERA_REQUEST -> {
                    selectedImageBitmap = data?.extras?.get("data") as? Bitmap
                    selectedImageUri = null
                    ivPreview.setImageBitmap(selectedImageBitmap)
                    ivPreview.visibility = View.VISIBLE
                    checkUploadReady()
                }
            }
        }
    }

    private fun checkUploadReady() {
        val hasImage = selectedImageUri != null || selectedImageBitmap != null
        val hasAngle = selectedAngle.isNotEmpty()
        val hasStage = selectedStage.isNotEmpty()
        btnUpload.isEnabled = hasImage && hasAngle && hasStage
    }

    private fun validateInputs(): Boolean {
        if (selectedImageUri == null && selectedImageBitmap == null) {
            showError("Lütfen bir fotoğraf seçin")
            return false
        }
        if (selectedAngle.isEmpty()) {
            showError("Lütfen açı seçin")
            return false
        }
        if (selectedStage.isEmpty()) {
            showError("Lütfen aşama seçin")
            return false
        }
        return true
    }

    private fun uploadPhoto() {
        progressBar.visibility = View.VISIBLE
        btnUpload.isEnabled = false

        val userId = auth.currentUser?.uid
        if (userId == null) {
            showError("Kullanıcı oturumu bulunamadı")
            return
        }

        val photoId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val fileName = "${treatmentId}_${selectedAngle}_${selectedStage}_$timestamp.jpg"
        val storageRef = storage.reference.child("treatments/$treatmentId/photos/$fileName")

        val byteArray = try {
            if (selectedImageUri != null) {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                compressBitmap(bitmap)
            } else {
                compressBitmap(selectedImageBitmap!!)
            }
        } catch (e: Exception) {
            Log.e("PhotoUpload", "Bitmap oluşturulamadı", e)
            showError("Görsel okunamadı")
            return
        }

        if (byteArray.isEmpty()) {
            showError("Görsel verisi boş, tekrar deneyin")
            return
        }

        storageRef.putBytes(byteArray)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    savePhotoToFirestore(photoId, uri.toString())
                }.addOnFailureListener { e ->
                    Log.e("PhotoUpload", "Download URL alınamadı", e)
                    showError("URL alınamadı")
                }
            }
            .addOnFailureListener { e ->
                Log.e("PhotoUpload", "Upload hatası", e)
                showError("Fotoğraf yüklenemedi, tekrar deneyin")
            }
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        var quality = 80
        val maxSize = 1500

        val ratio = Math.min(
            maxSize.toFloat() / bitmap.width,
            maxSize.toFloat() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)

        while (baos.toByteArray().size > 500_000 && quality > 30) {
            baos.reset()
            quality -= 10
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        }

        Log.d("PhotoUpload", "Final byte size: ${baos.size()}")
        return baos.toByteArray()
    }

    private fun savePhotoToFirestore(photoId: String, imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val notes = etNotes.text.toString()

        val photo = TreatmentPhoto(
            photoId = photoId,
            angle = selectedAngle,
            stage = selectedStage,
            imageUrl = imageUrl,
            thumbnailUrl = imageUrl,
            uploadedAt = Timestamp.now(),
            uploadedBy = userId,
            notes = notes
        )

        db.collection("treatments").document(treatmentId)
            .collection("photos").document(photoId)
            .set(photo.toMap())
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Fotoğraf başarıyla yüklendi", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("PhotoUpload", "Firestore kaydedilemedi", e)
                showError("Veritabanına kaydedilemedi")
            }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        btnUpload.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
