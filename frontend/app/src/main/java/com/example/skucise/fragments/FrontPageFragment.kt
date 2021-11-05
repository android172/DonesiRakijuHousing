package com.example.skucise.fragments

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val ctw : ContextThemeWrapper = ContextThemeWrapper(activity, R.style.Theme_SkuciSe_FrontpageTheme)
        val localInflater : LayoutInflater = inflater.cloneInContext(ctw)

        return localInflater.inflate(R.layout.fragment_frontpage, frc_page_body, false)
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
    }
}