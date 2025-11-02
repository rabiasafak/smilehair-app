package com.example.smilehair

import com.google.firebase.Timestamp

data class Treatment(
    val treatmentId: String = "",
    val userId: String = "",
    val treatmentType: String = "", // "hair_transplant", "beard_transplant", "eyebrow_transplant"
    val doctorName: String = "",
    val clinicName: String = "",
    val technique: String = "", // "FUE", "DHI", "Sapphire FUE", etc.
    val graftCount: String = "",
    val hairType: String = "", // "İnce", "Orta", "Kalın"
    val status: String = "active", // "active", "completed", "cancelled"
    val startDate: Timestamp? = null,
    val notes: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {

    /**
     * Tedavi tipini Türkçe olarak döndürür
     */
    fun getTreatmentTypeName(): String {
        return when (treatmentType) {
            "hair_transplant" -> "Saç Ekimi"
            "beard_transplant" -> "Sakal Ekimi"
            "eyebrow_transplant" -> "Kaş Ekimi"
            else -> "Tedavi"
        }
    }

    /**
     * Durum metnini Türkçe olarak döndürür
     */
    fun getStatusText(): String {
        return when (status) {
            "active" -> "Devam Ediyor"
            "completed" -> "Tamamlandı"
            "cancelled" -> "İptal Edildi"
            else -> "Bilinmiyor"
        }
    }

    /**
     * Duruma göre renk döndürür
     */
    fun getStatusColor(): Int {
        return when (status) {
            "active" -> R.color.status_active // Yeşil
            "completed" -> R.color.status_completed // Mavi
            "cancelled" -> R.color.status_cancelled // Kırmızı
            else -> R.color.gray
        }
    }

    /**
     * Tedavinin kaç gün önce başladığını hesaplar
     */
    fun getDaysFromStart(): Int {
        return startDate?.let {
            val now = System.currentTimeMillis()
            val startTime = it.toDate().time
            val diffInMillis = now - startTime
            (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        } ?: 0
    }

    /**
     * Tedavi başlangıç tarihini formatlar
     */
    fun getStartDateFormatted(): String {
        return startDate?.let {
            val sdf = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("tr"))
            sdf.format(it.toDate())
        } ?: "Tarih belirlenmedi"
    }

    /**
     * Tedavi süresini insan okunabilir formatta döndürür
     */
    fun getDurationText(): String {
        val days = getDaysFromStart()
        return when {
            days == 0 -> "Bugün başladı"
            days < 7 -> "$days gün önce"
            days < 30 -> "${days / 7} hafta önce"
            days < 365 -> "${days / 30} ay önce"
            else -> "${days / 365} yıl önce"
        }
    }

    /**
     * Firestore'a kaydetmek için Map'e dönüştürür
     */
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "treatmentId" to treatmentId,
            "userId" to userId,
            "treatmentType" to treatmentType,
            "doctorName" to doctorName,
            "clinicName" to clinicName,
            "technique" to technique,
            "graftCount" to graftCount,
            "hairType" to hairType,
            "status" to status,
            "startDate" to startDate,
            "notes" to notes,
            "createdAt" to createdAt
        )
    }

    companion object {
        /**
         * Tedavi tiplerini döndürür
         */
        fun getTreatmentTypes(): List<Pair<String, String>> {
            return listOf(
                "hair_transplant" to "Saç Ekimi",
                "beard_transplant" to "Sakal Ekimi",
                "eyebrow_transplant" to "Kaş Ekimi"
            )
        }

        /**
         * Teknikleri döndürür
         */
        fun getTechniques(): List<String> {
            return listOf(
                "FUE",
                "DHI",
                "Sapphire FUE",
                "Ice FUE",
                "Micro FUE"
            )
        }

        /**
         * Saç tiplerini döndürür
         */
        fun getHairTypes(): List<String> {
            return listOf(
                "İnce",
                "Orta",
                "Kalın"
            )
        }
    }
}