package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GalleryAdapter(private val items: List<Any>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = items[position]

        when (item) {
            is String -> {
                // Firebase URL
                Glide.with(holder.itemView.context)
                    .load(item)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_gallery)
                    .error(R.drawable.error_gallery)
                    .centerCrop()
                    .into(holder.galleryImage)
            }
            is Int -> {
                // Local drawable resource
                Glide.with(holder.itemView.context)
                    .load(item)
                    .centerCrop()
                    .into(holder.galleryImage)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val galleryImage: ImageView = itemView.findViewById(R.id.galleryImage)
    }
}
