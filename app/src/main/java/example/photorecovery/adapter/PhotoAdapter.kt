package com.example.photorecovery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photorecovery.R
import com.example.photorecovery.model.PhotoItem
import com.example.photorecovery.utils.FileUtils
import com.google.android.material.button.MaterialButton

class PhotoAdapter(
    private var photos: List<PhotoItem>,
    private val onDeleteClick: (PhotoItem) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        
        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(photo.file)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.ivPhoto)
        
        holder.tvFileName.text = "${photo.name} (${FileUtils.formatFileSize(photo.size)})"
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(photo)
        }
    }

    override fun getItemCount(): Int = photos.size

    fun updatePhotos(newPhotos: List<PhotoItem>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
