package com.example.smilehair

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val profileImageUrl: String? = null,
    val generalInfo: String? = null,
    val coreValues: String? = null,
    val workStyle: String? = null,
    val zodiacSign: String? = null,
    val favoritePerfume: String? = null,
    val inspiringCities: List<String>? = null
)
