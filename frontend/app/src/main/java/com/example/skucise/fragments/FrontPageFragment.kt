package com.example.skucise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.skucise.Util
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_frontpage.*
import org.json.JSONObject
import java.util.ArrayList

class FrontPageFragment : Fragment(R.layout.fragment_frontpage) {

    private lateinit var errorReport : Util.Companion.ErrorReport

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
        errorReport = Util.Companion.ErrorReport(tv_debug)

        btn_search_buy_options.setOnClickListener {
            val filters = FilterArray()
            filters.applyFilter(FilterArray.FilterNames.SaleType, FilterArray.SaleTypes.Purchase)

            val params = HashMap<String, String>()
            params["filterArray"] = filters.getFilters()

            ReqSender.sendRequestArray(
                this.activity,
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    errorReport.reportError("response:\n${response}")
                },
                { error ->
                    errorReport.reportError("error:\n$error")
                }
            )
        }

        btn_search_rent_options.setOnClickListener {
            val filters = FilterArray()
            filters.applyFilter(FilterArray.FilterNames.SaleType, FilterArray.SaleTypes.Rent)

            val params = HashMap<String, String>()
            params["filterArray"] = filters.getFilters()

            ReqSender.sendRequestArray(
                this.activity,
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    errorReport.reportError("response:\n${response}")
                },
                { error ->
                    errorReport.reportError("error:\n$error")
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