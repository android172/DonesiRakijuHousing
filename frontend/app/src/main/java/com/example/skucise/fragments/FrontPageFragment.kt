package com.example.skucise.fragments

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.example.skucise.*
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_frontpage.*
import kotlinx.android.synthetic.main.fragment_frontpage.view.*
import org.json.JSONArray
import kotlin.math.min

class FrontPageFragment : Fragment(R.layout.fragment_frontpage) {

    private var gradovi: JSONArray = JSONArray()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        ReqSender.sendRequestArray(
            requireActivity(),
            Request.Method.GET,
            "http://10.0.2.2:5000/api/advert/get_all_cities",
            null,
            { cities ->
                Toast.makeText(activity, "response:\n$cities", Toast.LENGTH_LONG).show()

            /*val btn: Button = Button(context)
            val cardViews : MutableList<CardView> = mutableListOf()
            val cl: ConstraintLayout = view.findViewById<ConstraintLayout>(R.id.hv_layout_container2)
            val cs = ConstraintSet()
            cs.connect(R.id.button_tmp, ConstraintSet.START, R.id.hv_layout_container2, ConstraintSet.)
            */
                gradovi = cities
            },
            { error ->
                Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frontpage, container, false)
        val tileSet = mutableListOf<TileSet>()
        for (i in 0..min(gradovi.length() / 3, 3)) {
            /*
        val card = CardView(requireContext())
        val city = cities[i].toString()

        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(660, 756)
        val margin1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, Resources.getSystem().displayMetrics)
        params.marginStart = 16
        params.marginEnd = 16
        params.topMargin = 16
        params.bottomMargin = 16

        card.layoutParams = params
        card.requestLayout()

        cs.clone(cl)
        cs.connect(card.id, ConstraintSet.TOP, cl.id, ConstraintSet.TOP)
        cs.connect(card.id, ConstraintSet.START, cl.id, ConstraintSet.START)

        cs.applyTo(cl)

        val img = ImageView(context)
        //img.id = "@+id/"
        val imgId = requireActivity().resources.getIdentifier(city, "drawable", requireActivity().packageName);
        img.setImageResource(imgId)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        card.addView(img)
        cl.addView(card)*/
            /*tileSet.add(
                TileSet(
                    "Beograd",
                    1,
                    "Nis",
                    2,
                    "Kragujevac",
                    3
                )
            )
            tileSet.add(
                TileSet(
                    "Beograd",
                    1,
                    "Nis",
                    2,
                    "Kragujevac",
                    3
                )
            )*/
            tileSet.add(
                TileSet(
                    gradovi[i].toString(),
                    i,
                    gradovi[i + 1].toString(),
                    i + 1,
                    gradovi[i + 2].toString(),
                    i + 2
                )
            )
        }

        val cityTilesAdapter = CityTilesAdapter(tileSet)
        val a = view.findViewById<RecyclerView>(R.id.rcv_city_tiles)
        if (a != null) {
            a.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            a.adapter = cityTilesAdapter
        }
        else{
            Toast.makeText(context, "greska!!!!!!!!!!!!!", Toast.LENGTH_LONG).show()
        }

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

    }
}