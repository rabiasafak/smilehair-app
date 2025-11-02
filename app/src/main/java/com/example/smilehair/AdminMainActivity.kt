package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.widget.ImageView
import com.bumptech.glide.Glide

class AdminMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTotalTreatments: TextView
    private lateinit var tvActiveTreatments: TextView
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var btnRefresh: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var cardUsers: MaterialCardView
    private lateinit var cardTreatments: MaterialCardView

    private val usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Admin kontrolü
        checkAdminAccess()

        initViews()
        setupListeners()
        loadDashboardData()
        loadUsers()
    }

    private fun initViews() {
        tvTotalUsers = findViewById(R.id.tvTotalUsers)
        tvTotalTreatments = findViewById(R.id.tvTotalTreatments)
        tvActiveTreatments = findViewById(R.id.tvActiveTreatments)
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnLogout = findViewById(R.id.btnLogout)
        cardUsers = findViewById(R.id.cardUsers)
        cardTreatments = findViewById(R.id.cardTreatments)

        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        btnRefresh.setOnClickListener {
            loadDashboardData()
            loadUsers()
            Toast.makeText(this, "Veriler yenilendi", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun checkAdminAccess() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Giriş yapılmamış!", Toast.LENGTH_SHORT).show()
            redirectToLogin()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Toast.makeText(this, "Kullanıcı bulunamadı!", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    redirectToLogin()
                    return@addOnSuccessListener
                }

                val role = document.getString("role")
                if (role != "admin") {
                    Toast.makeText(this, "Yetkiniz yok! Sadece adminler girebilir.", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    redirectToLogin()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                auth.signOut()
                redirectToLogin()
            }
    }

    private fun loadDashboardData() {
        // Toplam kullanıcı sayısı
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                tvTotalUsers.text = documents.size().toString()
            }

        // Toplam tedavi sayısı
        db.collection("treatments")
            .get()
            .addOnSuccessListener { documents ->
                tvTotalTreatments.text = documents.size().toString()
            }

        // Aktif tedavi sayısı
        db.collection("treatments")
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { documents ->
                tvActiveTreatments.text = documents.size().toString()
            }
    }

    private fun loadUsers() {
        db.collection("users")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                usersList.clear()
                for (doc in documents) {
                    doc.toObject(User::class.java)?.let {
                        usersList.add(it)
                    }
                }

                recyclerViewUsers.adapter = AdminUserAdapter(usersList) { user ->
                    showUserOptionsDialog(user)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminMain", "Error loading users", e)
                Toast.makeText(this, "Kullanıcılar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUserOptionsDialog(user: User) {
        val options = arrayOf(
            "Tedavi Ata",
            "Tedavileri Görüntüle",
            "Kullanıcı Bilgileri",
            "Admin Yap",
            "Kullanıcıyı Sil"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(user.displayName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openAssignTreatment(user)
                    1 -> viewUserTreatments(user)
                    2 -> showUserInfo(user)
                    3 -> makeUserAdmin(user)
                    4 -> confirmDeleteUser(user)
                }
            }
            .show()
    }

    private fun openAssignTreatment(user: User) {
        val intent = Intent(this, AdminAssignTreatmentActivity::class.java)
        intent.putExtra("userId", user.userId)
        intent.putExtra("userName", user.displayName)
        startActivity(intent)
    }

    private fun viewUserTreatments(user: User) {
        db.collection("treatments")
            .whereEqualTo("userId", user.userId)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("AdminMain", "Found ${documents.size()} treatments for ${user.displayName}")

                if (documents.isEmpty) {
                    Toast.makeText(this, "Bu kullanıcının tedavisi yok", Toast.LENGTH_SHORT).show()
                } else {
                    val treatmentsList = mutableListOf<Treatment>()
                    val treatmentNames = mutableListOf<String>()

                    for (doc in documents) {
                        val treatment = doc.toObject(Treatment::class.java)
                        treatmentsList.add(treatment)
                        val info = """
                            ${treatment.getTreatmentTypeName()}
                            Doktor: Dr. ${treatment.doctorName}
                            Durum: ${treatment.getStatusText()}
                            Tarih: ${treatment.getStartDateFormatted()}
                        """.trimIndent()
                        treatmentNames.add(info)
                    }

                    MaterialAlertDialogBuilder(this)
                        .setTitle("${user.displayName} - Tedaviler (${treatmentsList.size})")
                        .setItems(treatmentNames.toTypedArray()) { _, which ->
                            openTreatmentDetail(treatmentsList[which])
                        }
                        .setPositiveButton("Kapat", null)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminMain", "Error loading treatments", e)
                Toast.makeText(this, "Tedaviler yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openTreatmentDetail(treatment: Treatment) {
        val intent = Intent(this, TreatmentDetailActivity::class.java)
        intent.putExtra("treatmentId", treatment.treatmentId)
        startActivity(intent)
    }

    private fun showUserInfo(user: User) {
        val info = """
            Ad: ${user.displayName}
            Email: ${user.email}
            Telefon: ${user.phoneNumber.ifEmpty { "Belirtilmemiş" }}
            Cinsiyet: ${getGenderText(user.gender)}
            Kan Grubu: ${user.bloodType.ifEmpty { "Belirtilmemiş" }}
            Rol: ${getRoleText(user.role)}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Kullanıcı Bilgileri")
            .setMessage(info)
            .setPositiveButton("Tamam", null)
            .show()
    }

    private fun makeUserAdmin(user: User) {
        if (user.role == "admin") {
            Toast.makeText(this, "Kullanıcı zaten admin", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Admin Yap")
            .setMessage("${user.displayName} kullanıcısını admin yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                db.collection("users").document(user.userId)
                    .update("role", "admin")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Kullanıcı admin yapıldı", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun confirmDeleteUser(user: User) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Kullanıcıyı Sil")
            .setMessage("${user.displayName} kullanıcısını silmek istediğinize emin misiniz? Bu işlem geri alınamaz!")
            .setPositiveButton("Sil") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun deleteUser(user: User) {
        // Önce kullanıcının tedavilerini sil
        db.collection("treatments")
            .whereEqualTo("userId", user.userId)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    doc.reference.delete()
                }

                // Sonra kullanıcıyı sil
                db.collection("users").document(user.userId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Kullanıcı silindi", Toast.LENGTH_SHORT).show()
                        loadDashboardData()
                        loadUsers()
                    }
            }
    }

    private fun getGenderText(gender: String): String {
        return when (gender) {
            "male" -> "Erkek"
            "female" -> "Kadın"
            "other" -> "Diğer"
            else -> "Belirtilmemiş"
        }
    }

    private fun getRoleText(role: String): String {
        return when (role) {
            "admin" -> "Admin"
            "doctor" -> "Doktor"
            "patient" -> "Hasta"
            else -> "Bilinmiyor"
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Çıkış Yap")
            .setMessage("Admin panelinden çıkış yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                auth.signOut()
                redirectToLogin()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Sadece admin kontrolü geçtiyse veri yükle
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadDashboardData()
            loadUsers()
        }
    }
}