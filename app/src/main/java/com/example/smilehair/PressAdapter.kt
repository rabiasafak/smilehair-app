package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PressAdapter(private val pressList: List<PressNews>) :
    RecyclerView.Adapter<PressAdapter.PressViewHolder>() {

    inner class PressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pressImage: ImageView = itemView.findViewById(R.id.ivPressImage)
        val pressTitle: TextView = itemView.findViewById(R.id.tvPressTitle)
        val pressDate: TextView = itemView.findViewById(R.id.tvPressDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_press_card, parent, false)
        return PressViewHolder(view)
    }

    override fun onBindViewHolder(holder: PressViewHolder, position: Int) {
        val press = pressList[position]

        holder.pressTitle.text = press.title
        holder.pressDate.text = formatDate(press.date)

        // Görsel yükleme
        Glide.with(holder.itemView.context)
            .load(press.imageUrl)
            .placeholder(R.drawable.ic_info)
            .error(R.drawable.ic_info)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.pressImage)
    }

    private fun formatDate(date: String): String {
        // "2024-12-15" formatından "15 Aralık 2024" formatına çevir
        return try {
            val parts = date.split("-")
            val year = parts[0]
            val month = parts[1].toInt()
            val day = parts[2]

            val monthNames = arrayOf(
                "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
            )

            "$day ${monthNames[month - 1]} $year"
        } catch (e: Exception) {
            date // Hata durumunda orijinal tarihi döndür
        }
    }

    override fun getItemCount() = pressList.size
}