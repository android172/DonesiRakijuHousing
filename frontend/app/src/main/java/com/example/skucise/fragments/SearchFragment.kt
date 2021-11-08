package com.example.skucise.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skucise.*
import com.example.skucise.adapter.AdvertAdapter
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_search.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "advertsJsonArray"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private var adverts: ArrayList<Advert> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val jsonArray = JSONArray(it.getString(ARG_PARAM1))
            loadAdverts(jsonArray)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connecting adverts recycler view
        val advertAdapter = AdvertAdapter(adverts)
        rcv_search_adverts.adapter = advertAdapter
        rcv_search_adverts.layoutManager = LinearLayoutManager(activity)
    }

    fun loadAdverts(jsonArray: JSONArray) {
        adverts = ArrayList()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray[i] as JSONObject
            adverts.add( Advert(
                id                = json.getInt("id").toUInt(),
                title             = json.getString("title"),
                price             = json.getDouble("price"),
                city              = json.getString("city"),
                address           = json.getString("address"),
                size              = json.getDouble("size"),
                saleType          = SaleType.values()[json.getInt("saleType")]
            ))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(advertsJsonArray: JSONArray) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, advertsJsonArray.toString())
                }
            }
    }
}