package com.example.skucise.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.example.skucise.Advert
import com.example.skucise.R
import com.example.skucise.ReqSender
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.item_advert.*
import kotlinx.android.synthetic.main.item_advert.view.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AdvertAdapter(
    private var adverts: ArrayList<Advert> = ArrayList(),
    private var allFavorites: Boolean = false
) : RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder>() {

    class AdvertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var isFavorite: ArrayList<Boolean> = adverts.map { allFavorites } as ArrayList<Boolean>

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

            // favorite
            if (isFavorite[position])
                btn_add_to_favourites.setImageResource(R.drawable.ic_favourites_star_yellow_24)
            else
                btn_add_to_favourites.setImageResource(R.drawable.ic_favourites_star_gray_24)
            btn_add_to_favourites.setOnClickListener {
                val action = if(isFavorite[position]) "remove" else "add"
                ReqSender.sendRequestString(
                    context,
                    Request.Method.POST,
                    "http://10.0.2.2:5000/api/advert/${action}_favourite_advert",
                    hashMapOf(Pair("advertId", currentAdvert.id.toString())),
                    {
                        isFavorite[position] = !isFavorite[position]
                        notifyItemChanged(position)
                    },
                    { error ->
                        Toast.makeText(context, "error:\n$error", Toast.LENGTH_LONG).show()
                    }
                )
            }

            var images = currentAdvert.images.map { image ->
                "http://10.0.2.2:5000/api/image/get_advert_image_file?advertId=${currentAdvert.id}&imageName=$image"
            }
            if (images.isEmpty())
                images = arrayListOf("https://www.in4s.net/wp-content/uploads/2020/07/Beograd.jpg")

            val adapter = AdvertImagesAdapter(images)
            vpg_advert_images.adapter = adapter
            vpg_advert_images.offscreenPageLimit = 2
            indicator_vpg.setViewPager(vpg_advert_images)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdverts(adverts: ArrayList<Advert>) {
        this.adverts = adverts
        isFavorite = adverts.map { allFavorites } as ArrayList<Boolean>
        notifyDataSetChanged()
    }

    fun setupNavMenu(bottomNavigationView: BottomNavigationView) {
        this.navigationView = bottomNavigationView
    }

    fun addToFavorites(position: Int) {
        isFavorite[position] = true
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return adverts.size
    }
}