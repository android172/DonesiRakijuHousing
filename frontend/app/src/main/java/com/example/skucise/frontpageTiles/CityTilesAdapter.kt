package com.example.skucise.frontpageTiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skucise.R
import kotlinx.android.synthetic.main.item_city_tile.view.*

class CityTilesAdapter(private val tiles: List<TileSet>)
    : RecyclerView.Adapter<CityTilesAdapter.CityTilesViewHolder>() {

    class CityTilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val img = CityImageMap()

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
            //Toast.makeText(context, tiles[2].name1.lowercase().replace(' ', '_'), Toast.LENGTH_SHORT).show()

            tile_layout1.tv_tile1.text = tile.name1
            tile_layout1.imageView1.setImageResource(img.images.getValue(tile.name1))

            tile_layout2.tv_tile2.text = tile.name2
            tile_layout2.imageView2.setImageResource(img.images.getValue(tile.name2))

            tile_layout3.tv_tile3.text = tile.name3
            tile_layout3.imageView3.setImageResource(img.images.getValue(tile.name3))
        }
    }

    override fun getItemCount(): Int {
        return tiles.size
    }
}