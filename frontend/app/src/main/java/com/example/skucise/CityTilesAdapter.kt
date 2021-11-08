package com.example.skucise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_account_dropdown.view.*
import kotlinx.android.synthetic.main.item_city_tile.view.*

class CityTilesAdapter(private val tiles: List<TileSet>)
    : RecyclerView.Adapter<CityTilesAdapter.CityTilesViewHolder>() {

    class CityTilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityTilesViewHolder {
        return CityTilesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_city_tile,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CityTilesViewHolder, position: Int) {
        val tile = tiles[position]
        holder.itemView.apply {
            tile_layout1.tv_tile1.text = tile.name1
            tile_layout1.tv_tile1.id
            tile_layout2.tv_tile2.text = tile.name2
            tile_layout3.tv_tile3.text = tile.name3

            //val imgId = context.resources.getIdentifier(tile.name1, "drawable", requireActivity().packageName);
            tile_layout1.imageView.setImageResource(R.drawable.beograd)
            tile_layout2.imageView2.setImageResource(R.drawable.nis)
            tile_layout3.imageView3.setImageResource(R.drawable.kraguejvac)
        }
    }

    override fun getItemCount(): Int {
        return tiles.size
    }
}