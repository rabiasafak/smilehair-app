package com.example.smilehair

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoURL: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: Timestamp? = null,
    val gender: String = "", // "male", "female", "other"
    val bloodType: String = "",
    val allergies: List<String> = listOf(),

    // Tedavi öncesi fotoğraflar
    val beforePhotos: BeforePhotos = BeforePhotos(),

    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val role: String = "patient" // "patient", "doctor", "admin"
) {
    // Firestore için boş constructor gerekli
    constructor() : this(
        userId = "",
        email = "",
        displayName = "",
        photoURL = "",
        phoneNumber = "",
        dateOfBirth = null,
        gender = "",
        bloodType = "",
        allergies = listOf(),
        beforePhotos = BeforePhotos(),
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now(),
        role = "patient"
    )

    // Firestore'a kaydetmek için map'e çevir
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "displayName" to displayName,
            "photoURL" to photoURL,
            "phoneNumber" to phoneNumber,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender,
            "bloodType" to bloodType,
            "allergies" to allergies,
            "beforePhotos" to beforePhotos.toMap(),
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "role" to role
        )
    }
}

/**
 * Kullanıcının tedavi öncesi fotoğrafları
 */
data class BeforePhotos(
    val hairFront: String = "",        // Saç ön
    val hairTop: String = "",          // Saç üst
    val hairBack: String = "",         // Saç arka
    val beardFront: String = "",       // Sakal ön
    val beardSide: String = "",        // Sakal yan
    val eyebrowLeft: String = "",      // Kaş sol
    val eyebrowRight: String = "",     // Kaş sağ
    val mustacheFront: String = "",    // Bıyık ön
    val uploadedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "hairFront" to hairFront,
            "hairTop" to hairTop,
            "hairBack" to hairBack,
            "beardFront" to beardFront,
            "beardSide" to beardSide,
            "eyebrowLeft" to eyebrowLeft,
            "eyebrowRight" to eyebrowRight,
            "mustacheFront" to mustacheFront,
            "uploadedAt" to uploadedAt
        )
    }

    /**
     * En az bir fotoğraf yüklenmiş mi?
     */
    fun hasAnyPhoto(): Boolean {
        return hairFront.isNotEmpty() || hairTop.isNotEmpty() ||
                hairBack.isNotEmpty() || beardFront.isNotEmpty() ||
                beardSide.isNotEmpty() || eyebrowLeft.isNotEmpty() ||
                eyebrowRight.isNotEmpty() || mustacheFront.isNotEmpty()
    }
}