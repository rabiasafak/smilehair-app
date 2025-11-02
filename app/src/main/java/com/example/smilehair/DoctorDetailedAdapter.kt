package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class DoctorDetailedAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorDetailedAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.doctorName)
        val specialty: TextView = itemView.findViewById(R.id.doctorSpecialty)
        val profileImage: ImageView = itemView.findViewById(R.id.doctorImage)

        // Bilgi bölümleri
        val layoutGeneralInfo: LinearLayout = itemView.findViewById(R.id.layoutGeneralInfo)
        val generalInfo: TextView = itemView.findViewById(R.id.doctorGeneralInfo)
        val divider1: View = itemView.findViewById(R.id.divider1)

        val layoutCoreValues: LinearLayout = itemView.findViewById(R.id.layoutCoreValues)
        val coreValues: TextView = itemView.findViewById(R.id.doctorCoreValues)
        val divider2: View = itemView.findViewById(R.id.divider2)

        val layoutWorkStyle: LinearLayout = itemView.findViewById(R.id.layoutWorkStyle)
        val workStyle: TextView = itemView.findViewById(R.id.doctorWorkStyle)
        val divider3: View = itemView.findViewById(R.id.divider3)

        // Kişisel bilgiler
        val layoutPersonalInfo: LinearLayout = itemView.findViewById(R.id.layoutPersonalInfo)
        val layoutZodiac: LinearLayout = itemView.findViewById(R.id.layoutZodiac)
        val zodiac: TextView = itemView.findViewById(R.id.doctorZodiac)

        val layoutPerfume: LinearLayout = itemView.findViewById(R.id.layoutPerfume)
        val perfume: TextView = itemView.findViewById(R.id.doctorPerfume)

        val layoutCities: LinearLayout = itemView.findViewById(R.id.layoutCities)
        val cities: TextView = itemView.findViewById(R.id.doctorCities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_card, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]

        // Temel bilgiler
        holder.name.text = doctor.name
        holder.specialty.text = doctor.specialty

        // Profil resmi
        Glide.with(holder.itemView.context)
            .load(doctor.profileImageUrl)
            .placeholder(R.drawable.ic_info)
            .error(R.drawable.ic_info)
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(holder.profileImage)

        // Genel Bilgi
        if (!doctor.generalInfo.isNullOrBlank()) {
            holder.layoutGeneralInfo.visibility = View.VISIBLE
            holder.generalInfo.text = doctor.generalInfo
            holder.divider1.visibility = View.VISIBLE
        } else {
            holder.layoutGeneralInfo.visibility = View.GONE
            holder.divider1.visibility = View.GONE
        }

        // Temel Değerler
        if (!doctor.coreValues.isNullOrBlank()) {
            holder.layoutCoreValues.visibility = View.VISIBLE
            holder.coreValues.text = doctor.coreValues
            holder.divider2.visibility = View.VISIBLE
        } else {
            holder.layoutCoreValues.visibility = View.GONE
            holder.divider2.visibility = View.GONE
        }

        // Çalışma Tarzı
        if (!doctor.workStyle.isNullOrBlank()) {
            holder.layoutWorkStyle.visibility = View.VISIBLE
            holder.workStyle.text = doctor.workStyle
            holder.divider3.visibility = View.VISIBLE
        } else {
            holder.layoutWorkStyle.visibility = View.GONE
            holder.divider3.visibility = View.GONE
        }

        // Kişisel bilgileri kontrol et
        var hasPersonalInfo = false

        // Burç
        if (!doctor.zodiacSign.isNullOrBlank()) {
            holder.layoutZodiac.visibility = View.VISIBLE
            holder.zodiac.text = doctor.zodiacSign
            hasPersonalInfo = true
        } else {
            holder.layoutZodiac.visibility = View.GONE
        }

        // Parfüm
        if (!doctor.favoritePerfume.isNullOrBlank()) {
            holder.layoutPerfume.visibility = View.VISIBLE
            holder.perfume.text = doctor.favoritePerfume
            hasPersonalInfo = true
        } else {
            holder.layoutPerfume.visibility = View.GONE
        }

        // Şehirler
        if (!doctor.inspiringCities.isNullOrEmpty()) {
            holder.layoutCities.visibility = View.VISIBLE
            holder.cities.text = doctor.inspiringCities.joinToString(", ")
            hasPersonalInfo = true
        } else {
            holder.layoutCities.visibility = View.GONE
        }

        // Kişisel bilgiler bölümünü göster/gizle
        holder.layoutPersonalInfo.visibility = if (hasPersonalInfo) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = doctorList.size
}