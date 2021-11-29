package com.example.skucise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.example.skucise.adapter.AdvertImagesAdapter
import kotlinx.android.synthetic.main.activity_advert_images.*

class AdvertImagesActivity : AppCompatActivity() {

    var imageNames = ArrayList<String>()
    var imageUrls = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_images)

        val advertId = intent.getIntExtra("id", -1)

        val params = HashMap<String, String>()
        params["advertId"] = advertId.toString()

        ReqSender.sendRequestArray(
            this,
            Request.Method.GET,
            "http://10.0.2.2:5000/api/image/get_advert_image_names",
            params,
            { response ->
                imageNames = ArrayList()
                imageUrls = ArrayList()
                for (i in 0 until response.length()){
                    val splits = response[i].toString().split("\\")
                    imageNames.add(splits[splits.size - 1])
                    val imageName = splits[splits.size - 1]
                    imageUrls.add("http://10.0.2.2:5000/api/image/get_advert_image_file?advertId=${advertId}&imageName=$imageName")
                }
                rcv_advert_images.isNestedScrollingEnabled = false
                rcv_advert_images.adapter = AdvertImagesAdapter(imageUrls, 2)
                rcv_advert_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            },
            { error ->
                Toast.makeText(this, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )




    }
}