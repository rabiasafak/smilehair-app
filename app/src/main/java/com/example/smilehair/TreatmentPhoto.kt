package com.example.smilehair

import com.google.firebase.Timestamp
import java.util.Date

data class TreatmentPhoto(
    val photoId: String = "",
    val treatmentId: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val angle: String = "", // "front", "left", "right", "top", "back"
    val stage: String = "", // "before", "day1", "week1", "month1", vb.
    val uploadedAt: Timestamp = Timestamp(Date()),
    val uploadedBy: String = "",
    val notes: String = ""
) {

    companion object {
        /**
         * Tüm aşama seçeneklerini döndürür
         */
        fun getStageNames(): List<Pair<String, String>> {
            return listOf(
                "before" to "Öncesi",
                "day1" to "1. Gün",
                "day3" to "3. Gün",
                "week1" to "1. Hafta",
                "week2" to "2. Hafta",
                "month1" to "1. Ay",
                "month2" to "2. Ay",
                "month3" to "3. Ay",
                "month6" to "6. Ay",
                "month9" to "9. Ay",
                "month12" to "12. Ay"
            )
        }

        /**
         * Tüm açı seçeneklerini döndürür
         */
        fun getAngleNames(): List<Pair<String, String>> {
            return listOf(
                "front" to "Ön",
                "left" to "Sol",
                "right" to "Sağ",
                "top" to "Üst",
                "back" to "Arka"
            )
        }
    }

    /**
     * Açı adını Türkçe olarak döndürür
     */
    fun getAngleName(): String {
        return when (angle) {
            "front" -> "Ön"
            "left" -> "Sol"
            "right" -> "Sağ"
            "top" -> "Üst"
            "back" -> "Arka"
            else -> "Belirsiz"
        }
    }

    /**
     * Aşama adını Türkçe olarak döndürür
     */
    fun getStageName(): String {
        return when (stage) {
            "before" -> "Öncesi"
            "day1" -> "1. Gün"
            "day3" -> "3. Gün"
            "week1" -> "1. Hafta"
            "week2" -> "2. Hafta"
            "month1" -> "1. Ay"
            "month2" -> "2. Ay"
            "month3" -> "3. Ay"
            "month6" -> "6. Ay"
            "month9" -> "9. Ay"
            "month12" -> "12. Ay"
            else -> "Belirsiz"
        }
    }

    /**
     * Firestore'a kaydetmek için Map'e dönüştürür
     */
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "photoId" to photoId,
            "treatmentId" to treatmentId,
            "imageUrl" to imageUrl,
            "thumbnailUrl" to thumbnailUrl,
            "angle" to angle,
            "stage" to stage,
            "uploadedAt" to uploadedAt,
            "uploadedBy" to uploadedBy,
            "notes" to notes
        )
    }
}
