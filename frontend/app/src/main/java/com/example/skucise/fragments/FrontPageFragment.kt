package com.example.skucise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.example.skucise.FilterArray
import com.example.skucise.R
import com.example.skucise.ReqSender
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_frontpage.*
import kotlinx.android.synthetic.main.fragment_frontpage.view.*

class FrontPageFragment : Fragment(R.layout.fragment_frontpage) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frontpage, container, false)
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
                    Toast.makeText(activity, "response:\n$response", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(activity, "response:\n$response", Toast.LENGTH_LONG).show()
                },
                { error ->
                    Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }

        val btn: Button = Button(context)
        val cities : List<String> = listOf("beograd")//, "novi_sad", "nis", "kragujevac", "kraljevo", "krusevac", "subotica")
        val cardViews : MutableList<CardView> = mutableListOf()
        val cl: ConstraintLayout = view.findViewById<ConstraintLayout>(R.id.hv_layout_container2)
        val cs = ConstraintSet()
        //cs.connect(R.id.button_tmp, ConstraintSet.START, R.id.hv_layout_container2, ConstraintSet.)


        for (city in cities){
            val card: CardView = CardView(requireContext())

            cs.clone(cl)
            cs.connect(card.id, ConstraintSet.TOP, cl.id, ConstraintSet.TOP)
            cs.connect(card.id, ConstraintSet.START, cl.id, ConstraintSet.START)
            cs.applyTo(cl)

            val img: ImageView = ImageView(context)
            img.setImageResource(R.drawable.beograd)
            card.addView(img)
            cl.addView(card)
        }


    }
}