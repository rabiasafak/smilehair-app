package com.example.smilehair

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PressActivity : AppCompatActivity() {

    private lateinit var rvPress: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var tvPressCount: TextView
    private lateinit var btnBack: ImageButton

    private lateinit var pressAdapter: PressAdapter
    private val pressList = mutableListOf<PressNews>()

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_press)

        // 🔹 Firebase başlat
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        initViews()
        setupRecyclerView()
        setupClickListeners()

        // 🔹 Sadece 1 kez kullan: örnek haberleri Firestore'a ekler
        //addSamplePressToFirebase()

        // 🔹 Firestore'dan verileri yükle
        loadPressFromFirebase()
    }

    private fun initViews() {
        rvPress = findViewById(R.id.rvPress)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        tvPressCount = findViewById(R.id.tvPressCount)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        pressAdapter = PressAdapter(pressList)
        rvPress.apply {
            layoutManager = LinearLayoutManager(this@PressActivity)
            adapter = pressAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    // 🔹 Firestore'dan haberleri çek
    private fun loadPressFromFirebase() {
        showLoading(true)

        db.collection("press")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                pressList.clear()

                for (document in documents) {
                    val press = PressNews(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        date = document.getString("date") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                    pressList.add(press)
                }

                pressAdapter.notifyDataSetChanged()
                updatePressCount(pressList.size)
                showLoading(false)
                updateEmptyState()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(
                    this,
                    "Haberler yüklenirken hata oluştu: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                updateEmptyState()
            }
    }

    // 🔹 Firestore’a örnek haberleri ekler
    private fun addSamplePressToFirebase() {
        val pressNews = listOf(
            hashMapOf(
                "title" to "Klass Magazine, 2022 – January",
                "date" to "2022-01-01",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-1.jpg"
            ),
            hashMapOf(
                "title" to "Quality of Magazine, 2022 – January",
                "date" to "2022-01-20",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-2.jpg"
            ),
            hashMapOf(
                "title" to "Klass Magazine, 2022 – February",
                "date" to "2022-02-05",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-3.jpg"
            ),
            hashMapOf(
                "title" to "Kadıköy Life, 2022 – February",
                "date" to "2022-02-20",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-4.jpg"
            ),
            hashMapOf(
                "title" to "VIP Turkey, 2022 – March",
                "date" to "2022-03-12",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-5.jpg"
            ),
            hashMapOf(
                "title" to "Alem Magazine, 2022 – March",
                "date" to "2022-03-30",
                "imageUrl" to "https://www.smilehairclinic.com/wp-content/uploads/2023/09/basinda-biz-6.jpg"
            )
        )

        pressNews.forEach { press ->
            db.collection("press")
                .add(press)
                .addOnSuccessListener {
                    Toast.makeText(this, "Eklendi: ${press["title"]}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updatePressCount(count: Int) {
        tvPressCount.text = if (count > 0) {
            "Medyada yer alan $count haberimiz"
        } else {
            "Medyada yer alan haberlerimiz"
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvPress.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun updateEmptyState() {
        if (pressList.isEmpty() && !progressBar.isShown) {
            emptyState.visibility = View.VISIBLE
            rvPress.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvPress.visibility = View.VISIBLE
        }
    }
}
