package com.example.smilehair

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var galleryRecyclerView: RecyclerView

    private var galleryAutoScrollHandler: Handler? = null
    private var galleryAutoScrollRunnable: Runnable? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val galleryImageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.menuButton)
        val whatsappButton = findViewById<ExtendedFloatingActionButton>(R.id.whatsappButton)
        val readMoreButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.readMoreButton)

        readMoreButton.setOnClickListener {
            startActivity(Intent(this, HakkindaActivity::class.java))
        }

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        whatsappButton.setOnClickListener { openWhatsApp() }

        loadNavigationHeader()
        setupServicesWithArrows()
        loadGalleryFromFirestore()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }
                R.id.nav_guide -> {
                    startActivity(Intent(this, AfterProcessActivity::class.java))
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }
                R.id.nav_wp -> {
                    openWhatsApp()
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }
                R.id.nav_exit -> {
                    showExitDialog()
                    true
                }
                R.id.nav_patient_tracking -> {
                    checkAdminAndOpenTracking()
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadNavigationHeader() {
        val user = auth.currentUser
        if (user == null) return

        try {
            val headerView = navigationView.getHeaderView(0)
            val profileEmail = headerView.findViewById<TextView>(R.id.profile_email)
            val profileName = headerView.findViewById<TextView>(R.id.profile_name)
            val profileImage = headerView.findViewById<ImageView>(R.id.profile_image)

            profileEmail.text = user.email ?: "Email yok"

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: user.displayName ?: "Kullanıcı"
                        val profileUrl = document.getString("profilePhotoUrl")
                        profileName.text = name
                        if (!profileUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profileUrl).placeholder(R.drawable.smile).circleCrop().into(profileImage)
                        } else if (user.photoUrl != null) {
                            Glide.with(this).load(user.photoUrl).placeholder(R.drawable.smile).circleCrop().into(profileImage)
                        }

                        // Adminse Hasta Takip itemini görünür yap
                        val patientItem = navigationView.menu.findItem(R.id.nav_patient_tracking)
                        patientItem.isVisible = document.getString("role") == "admin"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Kullanıcı bilgileri yüklenemedi: ${e.message}", e)
                }

        } catch (e: Exception) {
            Log.e("MainActivity", "NavigationHeader yükleme hatası: ${e.message}", e)
        }
    }

    private fun setupServicesWithArrows() {
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView)
        val leftArrow = findViewById<FloatingActionButton>(R.id.servicesLeftArrow)
        val rightArrow = findViewById<FloatingActionButton>(R.id.servicesRightArrow)

        val services = listOf(
            Service("Saç Ekimi", R.drawable.ekim),
            Service("Sakal Ekimi", R.drawable.ekim),
            Service("Kaş Ekimi", R.drawable.kas),
            Service("Mezoterapi", R.drawable.mezoterapi),
            Service("Kadın Saç Ekimi", R.drawable.kadin),
            Service("Afro Saç Ekimi", R.drawable.afro),
            Service("Favori Ekimi", R.drawable.favori),
            Service("Bıyık Ekimi", R.drawable.favori)
        )

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        servicesRecyclerView.layoutManager = layoutManager

        servicesRecyclerView.adapter = ServicesAdapter(services) { service ->
            when (service.name) {
                "Saç Ekimi" -> startActivity(Intent(this, SacActivity::class.java))
                "Sakal Ekimi" -> startActivity(Intent(this, SakalActivity::class.java))
                "Kaş Ekimi" -> startActivity(Intent(this, KasActivity::class.java))
                "Mezoterapi" -> startActivity(Intent(this, MezoterapiActivity::class.java))
                "Kadın Saç Ekimi" -> startActivity(Intent(this, KadinSacActivity::class.java))
                "Afro Saç Ekimi" -> startActivity(Intent(this, AfroActivity::class.java))
                "Favori Ekimi" -> startActivity(Intent(this, FavoriActivity::class.java))
                "Bıyık Ekimi" -> startActivity(Intent(this, BiyikActivity::class.java))
            }
        }

        leftArrow.setOnClickListener { servicesRecyclerView.smoothScrollBy(-400, 0) }
        rightArrow.setOnClickListener { servicesRecyclerView.smoothScrollBy(400, 0) }

        leftArrow.visibility = View.GONE

        servicesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                leftArrow.visibility = if (!recyclerView.canScrollHorizontally(-1)) View.GONE else View.VISIBLE
                rightArrow.visibility = if (!recyclerView.canScrollHorizontally(1)) View.GONE else View.VISIBLE
            }
        })
    }

    private fun loadGalleryFromFirestore() {
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        db.collection("gallery").orderBy("order", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { documents ->
                galleryImageUrls.clear()
                if (documents.isEmpty) {
                    loadLocalGallery()
                    return@addOnSuccessListener
                }
                for (doc in documents) {
                    doc.getString("imageUrl")?.let { galleryImageUrls.add(it) }
                }
                if (galleryImageUrls.isNotEmpty()) setupGalleryRecyclerView() else loadLocalGallery()
            }
            .addOnFailureListener {
                loadLocalGallery()
            }
    }

    private fun setupGalleryRecyclerView() {
        galleryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        galleryRecyclerView.adapter = GalleryAdapter(galleryImageUrls)
        startAutoScroll()
    }

    private fun loadLocalGallery() {
        val localImages = listOf(R.drawable.img, R.drawable.img, R.drawable.img, R.drawable.img)
        galleryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        galleryRecyclerView.adapter = GalleryAdapter(localImages)
        startAutoScroll()
    }

    private fun startAutoScroll() {
        galleryAutoScrollHandler = Handler(Looper.getMainLooper())
        galleryAutoScrollRunnable = object : Runnable {
            private var currentPosition = 0
            override fun run() {
                val itemCount = galleryRecyclerView.adapter?.itemCount ?: 0
                if (itemCount > 0) {
                    currentPosition++
                    if (currentPosition >= itemCount) currentPosition = 0
                    galleryRecyclerView.smoothScrollToPosition(currentPosition)
                }
                galleryAutoScrollHandler?.postDelayed(this, 3000)
            }
        }
        galleryAutoScrollHandler?.postDelayed(galleryAutoScrollRunnable!!, 1000)
    }

    private fun openWhatsApp() {
        try {
            val phoneNumber = "+905491492400"
            val message = "Merhaba, SmileHair hakkında bilgi almak istiyorum."
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                auth.signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun checkAdminAndOpenTracking() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.getString("role") == "admin") {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                } else {
                    Toast.makeText(this, "Bu menüye sadece admin erişebilir", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadNavigationHeader()
    }

    override fun onDestroy() {
        super.onDestroy()
        galleryAutoScrollHandler?.removeCallbacks(galleryAutoScrollRunnable!!)
        galleryAutoScrollHandler = null
    }
}
