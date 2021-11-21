package com.example.skucise.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.skucise.Advert
import com.example.skucise.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.item_advert.view.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AdvertAdapter(
    private var adverts: ArrayList<Advert> = ArrayList(),
) : RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {

    class AdvertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var navigationView: BottomNavigationView? = null
    private lateinit var parentFragment : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertViewHolder {

        this.parentFragment = parent

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
            csl_advert_item.setOnClickListener {
                if (navigationView == null) return@setOnClickListener

                navigationView!!.menu.setGroupCheckable(0, true, false)
                for (i in 0 until navigationView!!.menu.size()) {
                    navigationView!!.menu.getItem(i).isChecked = false
                }
                navigationView!!.menu.setGroupCheckable(0, true, true)

                val args = Bundle()
                args.putInt("advertId", currentAdvert.id.toInt())
                parentFragment.findNavController().navigate(R.id.advertFragment, args)
            }
            tv_advert_title.text = currentAdvert.title
            tv_advert_date.text = currentAdvert.dateCreated.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            tv_advert_residence_type.text = currentAdvert.residenceType.toString()
            tv_advert_city.text = "${currentAdvert.city}, ${currentAdvert.address}"
            tv_advert_type.text = currentAdvert.saleType.toString()
            tv_advert_size.text = "${currentAdvert.size} kvadrata"
            tv_advert_price.text = "${currentAdvert.price} €"

            val images = mutableListOf(
                "https://www.in4s.net/wp-content/uploads/2020/07/Beograd.jpg",
                "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1d/6b/4b/85/caption.jpg?w=500&h=300&s=1&cx=2980&cy=1592&chk=v1_4c086a3f0079164b576b",
                "https://rs.n1info.com/wp-content/uploads/2021/04/KRAGUJEVAC-PANORAMA-IZVOR-N1-MILAN-NIKIC-scaled.jpg"
            )
            val adapter = AdvertImagesAdapter(images)
            vpg_advert_images.adapter = adapter
            indicator_vpg.setViewPager(vpg_advert_images)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdverts(adverts: ArrayList<Advert>) {
        this.adverts = adverts
        notifyDataSetChanged()
    }

    fun setupNavMenu(bottomNavigationView: BottomNavigationView) {
        this.navigationView = bottomNavigationView
    }

    override fun getItemCount(): Int {
        return adverts.size
    }
}