package com.example.skucise.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.skucise.Advert
import com.example.skucise.R
import kotlinx.android.synthetic.main.item_advert.view.*
import java.time.format.DateTimeFormatter

class AdvertAdapter(
    private var adverts: ArrayList<Advert> = ArrayList()
) : RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {

    class AdvertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {
        return AdvertViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_advert,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdvertViewHolder, position: Int) {
        val currentAdvert = adverts[position]
        holder.itemView.apply {
            tv_advert_title.text = currentAdvert.title
            tv_advert_date.text = currentAdvert.dateCreated.format(DateTimeFormatter.ISO_DATE)
            tv_advert_residence_type.text = currentAdvert.residenceType.toString()
            tv_advert_city.text = "${currentAdvert.city}, ${currentAdvert.address}"
            tv_advert_type.text = currentAdvert.saleType.toString()
            tv_advert_size.text = "${currentAdvert.size} kvadrata"
            tv_advert_price.text = "${currentAdvert.price} €"
            img_advert_current.clipToOutline = true
            val images = mutableListOf(
                "https://www.in4s.net/wp-content/uploads/2020/07/Beograd.jpg",
                "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1d/6b/4b/85/caption.jpg?w=500&h=300&s=1&cx=2980&cy=1592&chk=v1_4c086a3f0079164b576b",
                "https://rs.n1info.com/wp-content/uploads/2021/04/KRAGUJEVAC-PANORAMA-IZVOR-N1-MILAN-NIKIC-scaled.jpg"
            )
            val adapter = AdvertImagesAdapter(images)
            //val pager = this.findViewById<ViewPager2>(R.id.vpg_advert_images)
            vpg_advert_images.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdverts(adverts: ArrayList<Advert>) {
        this.adverts = adverts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return adverts.size
    }
}