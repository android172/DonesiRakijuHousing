package com.example.skucise.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.example.skucise.*
import com.example.skucise.adapter.AdvertAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
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
    private var filterViews: HashMap<String, Any>? = null
    private val advertAdapter : AdvertAdapter = AdvertAdapter(adverts)

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

        // load state
        if (advertsLoaded != null) {
            adverts = advertsLoaded!!
            advertAdapter.updateAdverts(adverts)
        }
        else {
            val params = HashMap<String, String>()
            params["filterArray"] = FilterArray().getFilters()

            ReqSender.sendRequestArray(
                this.requireActivity(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    adverts = loadAdverts(response)
                    fragmentState!!["search_query"] = adverts
                    advertAdapter.updateAdverts(adverts)
                    updateAdvertCount()
                },
                { error ->
                    Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
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
        rcv_search_adverts.apply {
            adapter = advertAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        updateAdvertCount()

        // Construct Filters
        btn_filters.setOnClickListener {
            csl_filters.visibility = View.VISIBLE

            if (filterViews == null) {
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
                val checkBoxStructureType = listCheckBoxes(
                    csl_filters_structure_type,
                    StructureType.values().asIterable()
                )

                // City
                var checkBoxCity : ArrayList<CheckBox> = ArrayList()
                if (allCities == null) {
                    ReqSender.sendRequestArray(
                        requireActivity(), Request.Method.GET,
                        "http://10.0.2.2:5000/api/advert/get_all_cities", null,
                        { cities ->
                            val cityArray = Array(
                                cities.length()
                            ) { i -> cities[i].toString() }
                            checkBoxCity = listCheckBoxes(csl_filters_city, cityArray.asIterable())
                            allCities = cityArray
                        },
                        { error ->
                            Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    checkBoxCity = listCheckBoxes(csl_filters_city, allCities!!.asIterable())
                }

                // Number of rooms
                val radioGroupNumOfRooms = createRadioGroup(
                    csl_filters_number_of_rooms,
                    arrayOf("1+", "2+", "3+", "4+", "5+")
                )

                // Number of bathrooms
                val radioGroupNumOfBathrooms = createRadioGroup(
                    csl_filters_number_of_bathrooms,
                    arrayOf("1+", "2+", "3+")
                )

                // furnished
                val radioGroupFurnished = createRadioGroup(
                    csl_filters_furnished,
                    arrayOf("Da", "Ne", "Nebitno")
                )

                filterViews = HashMap()
                filterViews!!["house"]     = checkBoxHouse
                filterViews!!["apartment"] = checkBoxApartment
                filterViews!!["buy"]       = checkBoxBuy
                filterViews!!["rent"]      = checkBoxRent
                filterViews!!["structure"] = checkBoxStructureType
                filterViews!!["city"]      = checkBoxCity
                filterViews!!["bedrooms"]  = radioGroupNumOfRooms
                filterViews!!["bathrooms"] = radioGroupNumOfBathrooms
                filterViews!!["furnished"] = radioGroupFurnished
            }
        }

        // Apply filters
        btn_filters_apply.setOnClickListener {
            csl_filters.visibility = View.GONE
            requestAdverts()
        }
    }

    @SuppressLint("NewApi")
    private fun loadAdverts(jsonArray: JSONArray): ArrayList<Advert> {
        val adverts = ArrayList<Advert>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray[i] as JSONObject
            adverts.add( Advert(
                id            = json.getInt("id").toUInt(),
                title         = json.getString("title"),
                price         = json.getDouble("price"),
                city          = json.getString("city"),
                address       = json.getString("address"),
                saleType      = SaleType.values()[json.getInt("saleType")],
                residenceType = ResidenceType.values()[json.getInt("residenceType")],
                size          = json.getDouble("size"),
                dateCreated   = LocalDateTime.parse(json.getString("dateCreated"))
            ))
        }
        return adverts
    }

    private fun updateAdvertCount() {
        val x = advertAdapter.itemCount
        tv_number_of_ads.text = "Broj pronadjenih oglasa: $x"
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

    private fun listCheckBoxes(into: ConstraintLayout, array: Iterable<Any>) : ArrayList<CheckBox> {
        val checkBoxList = ArrayList<CheckBox>()
        var previous : CheckBox? = null
        for (element in array) {
            val current = createCheckbox(into, element.toString())
            if (previous != null) {
                current.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = previous!!.id
                }
            }
            checkBoxList.add(current)
            previous = current
        }
        return checkBoxList
    }

    private fun checkBoxesAsArrayList(checkBoxes: ArrayList<CheckBox>): ArrayList<String> {
        val checkedCheckBoxes = ArrayList<String>()
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked) checkedCheckBoxes.add(checkBox.text.toString())
        }
        return checkedCheckBoxes
    }

    private fun createRadioGroup(into: ConstraintLayout, options: Array<String>, selected: Int = 0): RadioGroup {
        val radioGroup = RadioGroup(context)
        for (option in options) {
            val radioButton = RadioButton(context)
            radioButton.text = option
            radioGroup.addView(radioButton)
        }
        radioGroup.check(selected)
        into.addView(radioGroup)
        return radioGroup
    }

    private fun requestAdverts() {
        if (filterViews != null) {
            val checkBoxHouse            = filterViews!!["house"]     as CheckBox
            val checkBoxApartment        = filterViews!!["apartment"] as CheckBox
            val checkBoxBuy              = filterViews!!["buy"]       as CheckBox
            val checkBoxRent             = filterViews!!["rent"]      as CheckBox
            val checkBoxStructureType    = filterViews!!["structure"] as ArrayList<*>
            val checkBoxCity             = filterViews!!["city"]      as ArrayList<*>
            val radioGroupNumOfRooms     = filterViews!!["bedrooms"]  as RadioGroup
            val radioGroupNumOfBathrooms = filterViews!!["bathrooms"] as RadioGroup
            val radioGroupFurnished      = filterViews!!["furnished"] as RadioGroup

            // Apply filters where needed
            val filters = FilterArray()

            // Residence type
            val residenceType = checkBoxesAsArrayList(arrayListOf(checkBoxHouse, checkBoxApartment))
            if (residenceType.size == 1)
                filters.applyFilter(FilterArray.FilterNames.ResidenceType, ResidenceType.valueOf(residenceType[0]).ordinal)

            // Sale type
            val saleType = checkBoxesAsArrayList(arrayListOf(checkBoxBuy, checkBoxRent))
            if (saleType.size == 1)
                filters.applyFilter(FilterArray.FilterNames.SaleType, SaleType.valueOf(saleType[0]).ordinal)

            // Structure type
            val structureType = checkBoxesAsArrayList(checkBoxStructureType as ArrayList<CheckBox>)
            for (i in 0 until structureType.size)
                structureType[i] = StructureType.valueOf(structureType[i]).ordinal.toString()
            if (structureType.isNotEmpty())
                filters.applyFilter(FilterArray.FilterNames.StructureType, structureType)

            // City
            val city = checkBoxesAsArrayList(checkBoxCity as ArrayList<CheckBox>)
            if (city.isNotEmpty())
                filters.applyFilter(FilterArray.FilterNames.City, city)

            // Size
            var sendSize = false
            var sizeFrom = 0.0
            if (et_filters_size_from.text.toString().isNotBlank()) {
                sizeFrom = et_filters_size_from.text.toString().toDouble()
                sendSize = true
            }
            var sizeTo = Double.MAX_VALUE
            if (et_filters_size_to.text.toString().isNotBlank()) {
                sizeTo = et_filters_size_to.text.toString().toDouble()
                sendSize = true
            }
            if (sendSize)
                filters.applyFilter(FilterArray.FilterNames.Size, sizeFrom, sizeTo)

            // Price
            var sendPrice = false
            var priceFrom = 0.0
            if (et_filters_price_from.text.toString().isNotBlank()) {
                priceFrom = et_filters_price_from.text.toString().toDouble()
                sendPrice = true
            }
            var priceTo = Double.MAX_VALUE
            if (et_filters_price_to.text.toString().isNotBlank()) {
                priceTo = et_filters_price_to.text.toString().toDouble()
                sendPrice = true
            }
            if (sendPrice)
                filters.applyFilter(FilterArray.FilterNames.Price, priceFrom, priceTo)

            // Number of bedrooms
            if (radioGroupNumOfRooms.checkedRadioButtonId != 0) {
                val checkedRadio =
                    radioGroupNumOfRooms.findViewById<RadioButton>(radioGroupNumOfRooms.checkedRadioButtonId)
                filters.applyFilter(
                    FilterArray.FilterNames.NumBedrooms,
                    checkedRadio.text.toString().trimEnd('+').toInt()
                )
            }

            // Number of bathrooms
            if (radioGroupNumOfBathrooms.checkedRadioButtonId != 0) {
                val checkedRadio =
                    radioGroupNumOfBathrooms.findViewById<RadioButton>(radioGroupNumOfBathrooms.checkedRadioButtonId)
                filters.applyFilter(
                    FilterArray.FilterNames.NumBathrooms,
                    checkedRadio.text.toString().trimEnd('+').toInt()
                )
            }

            // Furnished
            if (radioGroupFurnished.checkedRadioButtonId != 0) {
                val checkedRadio =
                    radioGroupFurnished.findViewById<RadioButton>(radioGroupFurnished.checkedRadioButtonId)
                when(checkedRadio.text.toString()) {
                    "Da" -> filters.applyFilter(FilterArray.FilterNames.Furnished, "True")
                    "Ne" -> filters.applyFilter(FilterArray.FilterNames.Furnished, "False")
                }
            }

            val params = HashMap<String, String>()
            params["filterArray"] = filters.getFilters()

            ReqSender.sendRequestArray(
                this.requireActivity(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/search_adverts",
                params,
                { response ->
                    adverts = loadAdverts(response)
                    fragmentState!!["search_query"] = adverts
                    advertAdapter.updateAdverts(adverts)
                    updateAdvertCount()
                },
                { error ->
                    Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }
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