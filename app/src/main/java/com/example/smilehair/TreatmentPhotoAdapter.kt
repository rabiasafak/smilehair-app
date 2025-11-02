package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.text.SimpleDateFormat
import java.util.*

class TreatmentPhotoAdapter(
    private val photos: List<TreatmentPhoto>,
    private val onItemClick: (TreatmentPhoto) -> Unit
) : RecyclerView.Adapter<TreatmentPhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treatment_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    override fun getItemCount(): Int = photos.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
        private val tvPhotoStage: TextView = itemView.findViewById(R.id.tvPhotoStage)
        private val tvPhotoAngle: TextView = itemView.findViewById(R.id.tvPhotoAngle)
        private val tvPhotoDate: TextView = itemView.findViewById(R.id.tvPhotoDate)

        fun bind(photo: TreatmentPhoto) {
            // Fotoğrafı yükle
            Glide.with(itemView.context)
                .load(photo.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_gallery)
                .error(R.drawable.error_gallery)
                .centerCrop()
                .into(ivPhoto)

            // Bilgileri göster
            tvPhotoStage.text = photo.getStageName()
            tvPhotoAngle.text = photo.getAngleName()

            // Tarihi formatla
            val sdf = SimpleDateFormat("dd MMM", Locale("tr"))
            tvPhotoDate.text = sdf.format(photo.uploadedAt.toDate())

            // Tıklama
            itemView.setOnClickListener {
                onItemClick(photo)
            }
        }
    }
}