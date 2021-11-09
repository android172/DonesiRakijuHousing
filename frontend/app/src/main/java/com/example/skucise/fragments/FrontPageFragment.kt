package com.example.skucise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.example.skucise.*
import com.example.skucise.frontpageTiles.CityTilesAdapter
import com.example.skucise.frontpageTiles.TileSet
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_frontpage.*
import kotlinx.android.synthetic.main.fragment_frontpage.view.*
import kotlin.math.min

class FrontPageFragment : Fragment(R.layout.fragment_frontpage) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frontpage, container, false)
        val tileSet = mutableListOf<TileSet>()
        ReqSender.sendRequestArray(
            requireActivity(),
            Request.Method.GET,
            "http://10.0.2.2:5000/api/advert/get_all_cities",
            null,
            { cities ->

                /*var s = ""
                for (i in 0 until cities.length()){
                    println()
                    s += "\"" + cities[i].toString() + "\" to R.drawable." + cities[i].toString().lowercase().replace(' ', '_') + ",\n"
                }
                Log.i("tag", s)*/
                for (i in 0 until min(cities.length() / 3, 5)) {
                    tileSet.add(
                        TileSet(
                            cities[3 * i].toString(),
                            3 * i,
                            cities[3 * i + 1].toString(),
                            3 * i + 1,
                            cities[3 * i + 2].toString(),
                            3 * i + 2
                        )
                    )
                }
                //Toast.makeText(context, "test: mounted = " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show()
                val cityTilesAdapter = CityTilesAdapter(tileSet, requireActivity().nav_bottom_navigator)
                val a = view.findViewById<RecyclerView>(R.id.rcv_city_tiles)
                a.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                a.adapter = cityTilesAdapter
            },
            { error ->
                Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hv_tiles.tile_layout1.setOnClickListener {
            Toast.makeText(this.activity, "Test", Toast.LENGTH_LONG).show()
        }

        btn_search_buy_options.setOnClickListener {
            val filters = FilterArray()
            filters.applyFilter(FilterArray.FilterNames.SaleType, FilterArray.SaleTypes.Purchase)

            val params = HashMap<String, String>()
            params["filterArray"] = filters.getFilters()

            ReqSender.sendRequestArray(
                this.requireActivity(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    val args = Bundle()
                    args.putString("advertsJsonArray", response.toString())
                    findNavController().navigate(requireActivity().nav_bottom_navigator.menu[1].itemId, args)
                },
                { error ->
                    Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }

        btn_search_rent_options.setOnClickListener {
            val filters = FilterArray()
            filters.applyFilter(FilterArray.FilterNames.SaleType, FilterArray.SaleTypes.Rent)

            val params = HashMap<String, String>()
            params["filterArray"] = filters.getFilters()

            ReqSender.sendRequestArray(
                this.requireActivity(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    val args = Bundle()
                    args.putString("advertsJsonArray", response.toString())
                    findNavController().navigate(requireActivity().nav_bottom_navigator.menu[1].itemId, args)
                },
                { error ->
                    Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }

    }
}