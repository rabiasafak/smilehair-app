package com.example.smilehair

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView

class AdminUserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivUserPhoto: CircleImageView = itemView.findViewById(R.id.ivUserPhoto)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val tvUserPhone: TextView = itemView.findViewById(R.id.tvUserPhone)
        private val chipRole: Chip = itemView.findViewById(R.id.chipRole)

        fun bind(user: User) {
            tvUserName.text = user.displayName.ifEmpty { "İsimsiz Kullanıcı" }
            tvUserEmail.text = user.email
            tvUserPhone.text = user.phoneNumber.ifEmpty { "Telefon yok" }

            // Profil fotoğrafı
            if (user.photoURL.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(user.photoURL)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(ivUserPhoto)
            } else {
                ivUserPhoto.setImageResource(R.drawable.ic_profile_placeholder)
            }

            // Rol chip'i
            when (user.role) {
                "admin" -> {
                    chipRole.text = "Admin"
                    chipRole.setChipBackgroundColorResource(R.color.status_cancelled)
                }
                "doctor" -> {
                    chipRole.text = "Doktor"
                    chipRole.setChipBackgroundColorResource(R.color.status_completed)
                }
                else -> {
                    chipRole.text = "Hasta"
                    chipRole.setChipBackgroundColorResource(R.color.status_active)
                }
            }

            // Kullanıcı tıklayınca dialog ile bilgileri ve fotoğrafları göster
            itemView.setOnClickListener {
                showUserInfo(user)
                onUserClick(user) // Eğer ayrıca activity/fragment callback istiyorsan
            }
        }

        // Kullanıcı bilgilerini ve fotoğrafları göster
        private fun showUserInfo(user: User) {
            val context = itemView.context
            val info = """
                Ad: ${user.displayName}
                Email: ${user.email}
                Telefon: ${user.phoneNumber.ifEmpty { "Belirtilmemiş" }}
                Cinsiyet: ${getGenderText(user.gender)}
                Kan Grubu: ${user.bloodType.ifEmpty { "Belirtilmemiş" }}
                Rol: ${getRoleText(user.role)}
                
                Tedavi Öncesi Fotoğraflar:
                ${if (user.beforePhotos.hasAnyPhoto()) "✓ Yüklendi" else "✗ Yüklenmedi"}
            """.trimIndent()

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle("Kullanıcı Bilgileri")
                .setMessage(info)
                .setPositiveButton("Tamam", null)

            if (user.beforePhotos.hasAnyPhoto()) {
                dialog.setNeutralButton("Fotoğrafları Gör") { _, _ ->
                    showUserPhotos(context, user)
                }
            }

            dialog.show()
        }

        // Tedavi öncesi fotoğrafları göster
        private fun showUserPhotos(context: android.content.Context, user: User) {
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_user_photos, null)

            val ivHair = dialogView.findViewById<ImageView>(R.id.ivDialogHair)
            val ivBeard = dialogView.findViewById<ImageView>(R.id.ivDialogBeard)
            val ivMustache = dialogView.findViewById<ImageView>(R.id.ivDialogMustache)

            if (user.beforePhotos.hairFront.isNotEmpty()) {
                Glide.with(context).load(user.beforePhotos.hairFront).into(ivHair)
                ivHair.visibility = View.VISIBLE
            }

            if (user.beforePhotos.beardFront.isNotEmpty()) {
                Glide.with(context).load(user.beforePhotos.beardFront).into(ivBeard)
                ivBeard.visibility = View.VISIBLE
            }

            if (user.beforePhotos.mustacheFront.isNotEmpty()) {
                Glide.with(context).load(user.beforePhotos.mustacheFront).into(ivMustache)
                ivMustache.visibility = View.VISIBLE
            }

            MaterialAlertDialogBuilder(context)
                .setTitle("${user.displayName} - Tedavi Öncesi Fotoğraflar")
                .setView(dialogView)
                .setPositiveButton("Kapat", null)
                .show()
        }
    }

    // Gender ve role textleri için yardımcı fonksiyonlar
    private fun getGenderText(gender: String): String {
        return when (gender.lowercase()) {
            "male" -> "Erkek"
            "female" -> "Kadın"
            "other" -> "Diğer"
            else -> "Belirtilmemiş"
        }
    }

    private fun getRoleText(role: String): String {
        return when (role.lowercase()) {
            "admin" -> "Admin"
            "doctor" -> "Doktor"
            else -> "Hasta"
        }
    }
}
