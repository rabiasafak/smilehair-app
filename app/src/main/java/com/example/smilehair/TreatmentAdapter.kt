package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class TreatmentAdapter(
    private val treatments: List<Treatment>,
    private val onItemClick: (Treatment) -> Unit
) : RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder>() {

    inner class TreatmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardTreatment)
        val tvTreatmentType: TextView = view.findViewById(R.id.tvTreatmentType)
        val tvDoctorName: TextView = view.findViewById(R.id.tvDoctorName)
        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvTechnique: TextView = view.findViewById(R.id.tvTechnique)
        val tvGraftCount: TextView = view.findViewById(R.id.tvGraftCount)
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treatment, parent, false)
        return TreatmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        val treatment = treatments[position]

        holder.tvTreatmentType.text = treatment.getTreatmentTypeName()
        holder.tvDoctorName.text = "Dr. ${treatment.doctorName}"
        holder.tvStartDate.text = treatment.getDurationText()
        holder.tvTechnique.text = treatment.technique
        holder.tvGraftCount.text = "${treatment.graftCount} Greft"

        holder.chipStatus.text = treatment.getStatusText()
        holder.chipStatus.setChipBackgroundColorResource(treatment.getStatusColor())

        holder.cardView.setOnClickListener {
            onItemClick(treatment)
        }
    }

    override fun getItemCount() = treatments.size
}