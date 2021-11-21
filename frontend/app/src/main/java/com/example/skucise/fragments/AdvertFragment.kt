package com.example.skucise.fragments

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.example.skucise.*
import com.example.skucise.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_advert.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "advertId"

/**
 * A simple [Fragment] subclass.
 * Use the [AdvertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdvertFragment : Fragment(), OnMapReadyCallback {
    private var advert: Advert? = null
    private var averageScore: String = "Bez ocena"
    private var canLeaveReview: Boolean = false

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val advertId = it.getInt(ARG_PARAM1)

            val params = HashMap<String, String>()
            params["advertId"] = advertId.toString()

            ReqSender.sendRequest(
                requireContext(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/advert/get_advert",
                params,
                { response ->
                    val advertData = response.getJSONObject("advertData")
                    advert = Advert(
                        id                = advertData.getInt("id").toUInt(),
                        residenceType     = ResidenceType.values()[advertData.getInt("residenceType")],
                        saleType          = SaleType.values()[advertData.getInt("saleType")],
                        structureType     = StructureType.values()[advertData.getInt("structureType")],
                        title             = advertData.getString("title"),
                        description       = advertData.getString("description"),
                        city              = advertData.getString("city"),
                        address           = advertData.getString("address"),
                        size              = advertData.getDouble("size"),
                        price             = advertData.getDouble("price"),
                        ownerId           = advertData.getInt("ownerId").toUInt(),
                        numberOfBedrooms  = advertData.getInt("numBedrooms").toUInt(),
                        numberOfBathrooms = advertData.getInt("numBathrooms").toUInt(),
                        furnished         = advertData.getBoolean("furnished"),
                        yearOfMake        = advertData.getInt("yearOfMake").toUInt(),
                        dateCreated       = LocalDateTime.parse(advertData.getString("dateCreated"))
                    )
                    averageScore = response.getString("averageScore")
                    if (averageScore == "Not rated.") averageScore = "Bez ocena"
                    canLeaveReview = response.getBoolean("canLeaveReview")

                    if (tv_advert_page_sale_type != null)
                        updateAdvertInfo()
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_advert, container, false)

        // Initialize map
        val mapView = view.findViewById(R.id.map_advert_page_location) as MapView
        mapView.onCreate(null)
        mapView.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update page look
        updateAdvertInfo()
    }

    override fun onResume() {
        super.onResume()
        if (map_advert_page_location != null)
            map_advert_page_location.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (map_advert_page_location != null)
            map_advert_page_location.onPause()
    }

    override fun onDestroy() {
        if (map_advert_page_location != null)
            map_advert_page_location.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (map_advert_page_location != null)
            map_advert_page_location.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        with(map) {
            val position = LatLng(-33.920455, 18.466941)
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
            setOnMapClickListener {
                Toast.makeText(requireContext(), "Clicked on map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun updateAdvertInfo() {
        if (advert == null) return

        // Updating text
        tv_advert_page_sale_type.text = if (advert!!.saleType == SaleType.Prodaja) "NA PRODAJU" else "IZDAJE SE"
        tv_advert_page_title.text     = advert!!.title
        tv_advert_page_avr_rew.text   = averageScore
        tv_advert_page_date.text      = advert!!.dateCreated.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        tv_advert_page_city.text      = "${advert!!.city},"
        tv_advert_page_address.text   = advert!!.address
        tv_advert_page_type.text      = "${advert!!.residenceType} iz ${advert!!.yearOfMake}; ${advert!!.structureType}"
        tv_advert_page_price.text     = "${advert!!.price} €"
        tv_advert_page_bedrooms.text  = "${advert!!.numberOfBedrooms} spavaće sobe"
        tv_advert_page_bathrooms.text = "${advert!!.numberOfBathrooms} kupatila"
        tv_advert_page_size.text      = "${advert!!.size} kvadrata"
        tv_advert_page_furnished.text = if (advert!!.furnished) "Sa nameštajem" else "Bez nameštaja"
        tv_advert_page_details.text   = advert!!.description

        // Updating map
        val geocoder = Geocoder(requireContext())
        val searchQuery = "${advert!!.city}, ${advert!!.address}"
        val addressList = geocoder.getFromLocationName(searchQuery, 1)
        if (addressList.size > 0) {
            val address = addressList[0]
            val position = LatLng(address.latitude, address.longitude)
            map_advert_page_location.getMapAsync { map ->
                with(map) {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13f))
                    addMarker(MarkerOptions().position(position))
                    mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param advertId Parameter 1.
         * @return A new instance of fragment AdvertFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(advertId: Int) =
            AdvertFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, advertId)
                }
            }
    }
}