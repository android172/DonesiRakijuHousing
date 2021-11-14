package com.example.skucise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skucise.Advert
import com.example.skucise.R
import kotlinx.android.synthetic.main.advert_image_item.view.*

class AdvertImagesAdapter (
    private val images: List<String> = ArrayList()
) : RecyclerView.Adapter<AdvertImagesAdapter.AdvertImageViewHolder>() {

    inner class AdvertImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvertImagesAdapter.AdvertImageViewHolder {

        return AdvertImageViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.advert_image_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdvertImageViewHolder, position: Int) {
        val image = images[position]
        holder.itemView.apply {
            //img_advert.setImageResource(R.drawable.beograd)
            img_advert.clipToOutline = true
            Glide.with(context)
                .load(image)

                .placeholder(R.drawable.beograd)
                .into(img_advert)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}