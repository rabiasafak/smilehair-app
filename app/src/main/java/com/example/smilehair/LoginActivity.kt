package com.example.smilehair

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    // Email/Şifre Views
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnEmailLogin: Button
    private lateinit var btnEmailRegister: Button
    private lateinit var btnGoogleSignIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase başlat
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnEmailLogin = findViewById(R.id.btnEmailLogin)
        btnEmailRegister = findViewById(R.id.btnEmailRegister)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)

        // Google Sign-In config
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Listeners
        btnGoogleSignIn.setOnClickListener { signInWithGoogle() }
        btnEmailLogin.setOnClickListener { handleEmailLogin() }
        btnEmailRegister.setOnClickListener { registerWithEmail() }

        // Zaten giriş yapılmışsa kullanıcı yönlendir
        auth.currentUser?.let {
            if (it.email == "admin@smilehair.com") {
                startActivity(Intent(this, AdminMainActivity::class.java))
                finish()
            } else {
                handleUserNavigation(it.uid, it.email ?: "")
            }
        }
    }

    private fun handleEmailLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve şifre giriniz", Toast.LENGTH_SHORT).show()
            return
        }

        // Admin kontrolü
        if (email == "admin" && password == "admin") {
            loginAsAdmin()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { handleUserNavigation(it.uid, it.email ?: "") }
                } else {
                    Log.e("LoginActivity", "Email login failed", task.exception)
                    Toast.makeText(this, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginAsAdmin() {
        auth.signInWithEmailAndPassword("admin@smilehair.com", "Admin123!")
            .addOnSuccessListener {
                startActivity(Intent(this, AdminMainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                createAdminAccount()
            }
    }

    private fun createAdminAccount() {
        auth.createUserWithEmailAndPassword("admin@smilehair.com", "Admin123!")
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                val adminUser = User(userId, "admin@smilehair.com", "Admin", "", "admin")
                db.collection("users").document(userId).set(adminUser.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Admin hesabı oluşturuldu", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AdminMainActivity::class.java))
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Admin girişi başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registerWithEmail() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve şifre giriniz", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { createUserProfile(it.uid, it.email ?: "", it.email?.substringBefore("@") ?: "Kullanıcı", "") }
                } else {
                    Log.e("LoginActivity", "Registration failed", task.exception)
                    Toast.makeText(this, "Kayıt başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("LoginActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google girişi başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let { handleUserNavigation(it.uid, it.email ?: "") }
            } else {
                Toast.makeText(this, "Giriş başarısız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUserNavigation(userId: String, email: String) {
        if (email == "admin@smilehair.com") {
            // Admin kullanıcı için Firestore sorgusu yapmadan direkt yönlendir
            startActivity(Intent(this, AdminMainActivity::class.java))
            finish()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    if (user != null && isProfileComplete(user)) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this, ProfileActivity::class.java).apply { putExtra("isFirstLogin", true) })
                        finish()
                    }
                } else {
                    createUserProfile(userId, email, email.substringBefore("@"), "")
                }
            }
    }

    private fun createUserProfile(userId: String, email: String, displayName: String, photoURL: String) {
        val newUser = User(userId, email, displayName, photoURL, "patient")
        db.collection("users").document(userId).set(newUser.toMap())
            .addOnSuccessListener {
                startActivity(Intent(this, ProfileActivity::class.java).apply { putExtra("isFirstLogin", true) })
                finish()
            }
    }

    private fun isProfileComplete(user: User): Boolean {
        return user.phoneNumber.isNotEmpty() && user.gender.isNotEmpty() && user.dateOfBirth != null
    }
}
