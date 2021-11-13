package com.example.skucise.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.example.skucise.*
import com.example.skucise.adapter.AdvertAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

        var advertsLoaded : ArrayList<Advert>? = null

        // load previous state if it exists
        if (fragmentState != null) {
            val list = fragmentState!!["search_query"]
            advertsLoaded = (list as ArrayList<*>)
                .filterIsInstance<Advert>()
                .takeIf { it.size == list.size } as ArrayList<Advert>?
        } else {
            fragmentState = HashMap()
            fragmentState!!["search_query"] = ArrayList<Advert>()
        }

        // if there have been new arguments sent they take priority
        arguments?.let {
            val jsonArray = JSONArray(it.getString(ARG_PARAM1))
            advertsLoaded = loadAdverts(jsonArray)
            if (advertsLoaded != null)
                fragmentState!!["search_query"] = advertsLoaded!!
        }

        if (advertsLoaded != null)
            adverts = advertsLoaded!!
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
        /*scv_search_scroll.csl_search_scroll_box.*/rcv_search_adverts.apply {
            adapter = advertAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        // Construct Filters
        btn_filters.setOnClickListener {
            csl_filters.visibility = View.VISIBLE

            // Residence type
            val checkBoxHouse = createCheckbox(csl_filters_residence_type, "Kuća")
            val checkBoxApartment = createCheckbox(csl_filters_residence_type, "Stan")
            checkBoxApartment.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToEnd = checkBoxHouse.id
            }

            // Sale type
            val checkBoxBuy = createCheckbox(csl_filters_sale_type, "Prodaja")
            val checkBoxRent = createCheckbox(csl_filters_sale_type, "Iznajmljivanje")
            checkBoxRent.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToEnd = checkBoxBuy.id
            }

            // Structure type
            listCheckBoxes(csl_filters_structure_type, StructureType.values().asIterable())

            // City
            if (allCities == null) {
                ReqSender.sendRequestArray(
                    requireActivity(), Request.Method.GET,
                    "http://10.0.2.2:5000/api/advert/get_all_cities", null,
                    { cities ->
                        val cityArray = Array(
                            cities.length()
                        ) { i -> cities[i].toString() }
                        listCheckBoxes(csl_filters_city, cityArray.asIterable())
                        allCities = cityArray
                    },
                    { error ->
                        Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                listCheckBoxes(csl_filters_city, allCities!!.asIterable())
            }

            // Number of rooms
            createRadioGroup(csl_filters_number_of_rooms, arrayOf("1+", "2+", "3+", "4+", "5+"))

            // Number of bathrooms
            createRadioGroup(csl_filters_number_of_bathrooms, arrayOf("1+", "2+", "3+"))

            // furnished
            createRadioGroup(csl_filters_furnished, arrayOf("Da", "Ne", "Nebitno"))
        }

        // Apply filters
        btn_filters_apply.setOnClickListener {
            csl_filters.visibility = View.GONE
        }
    }

    private fun loadAdverts(jsonArray: JSONArray): ArrayList<Advert> {
        val adverts = ArrayList<Advert>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray[i] as JSONObject
            adverts.add( Advert(
                id       = json.getInt("id").toUInt(),
                title    = json.getString("title"),
                price    = json.getDouble("price"),
                city     = json.getString("city"),
                address  = json.getString("address"),
                saleType = SaleType.values()[json.getInt("saleType")],
                size     = json.getDouble("size")
            ))
        }
        return adverts
    }

    private fun createCheckbox(into: ConstraintLayout, name: String, checked: Boolean = false) : CheckBox {
        val checkBox = CheckBox(context)
        checkBox.id = View.generateViewId()
        checkBox.text = name
        checkBox.isChecked = checked
        checkBox.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        into.addView(checkBox)
        return checkBox
    }

    private fun listCheckBoxes(into: ConstraintLayout, array: Iterable<Any>) {
        var previus : CheckBox? = null
        for (element in array) {
            val current = createCheckbox(into, element.toString())
            if (previus != null) {
                current.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = previus!!.id
                }
            }
            previus = current
        }
    }

    private fun createRadioGroup(into: ConstraintLayout, options: Array<String>, selected: Int = 0) {
        val radioGroup = RadioGroup(context)
        for (option in options) {
            val radioButton = RadioButton(context)
            radioButton.text = option
            radioGroup.addView(radioButton)
        }
        radioGroup.check(selected)
        into.addView(radioGroup)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param advertsJsonArray Parameter 1.
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


        var fragmentState : HashMap<String, Any>? = null
        var allCities : Array<String>? = null
    }
}