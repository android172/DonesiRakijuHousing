package com.example.skucise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skucise.Advert
import com.example.skucise.R
import kotlinx.android.synthetic.main.item_advert.view.*

class AdvertAdapter(
    private val adverts: ArrayList<Advert> = ArrayList()
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

    override fun onBindViewHolder(holder: AdvertViewHolder, position: Int) {
        val currentAdvert = adverts[position]
        holder.itemView.apply {
            tv_advert_title.text = currentAdvert.title
            tv_advert_city.text = "${currentAdvert.city}, ${currentAdvert.address}"
            tv_advert_type.text = currentAdvert.saleType.toString()
            tv_advert_size.text = "${currentAdvert.size} kvadrata"
            tv_advert_price.text = "${currentAdvert.price} €"
        }
    }

    override fun getItemCount(): Int {
        return adverts.size
    }
}