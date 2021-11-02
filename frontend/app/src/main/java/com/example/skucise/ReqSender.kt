package com.example.skucise

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject


class ReqSender {
    companion object {
        fun sendRequest(
            context: Context?,
            method : Int,
            url : String,
            params : MutableMap<String, String>?,
            listener: Response.Listener<JSONObject>,
            errorListener: Response.ErrorListener?,
        ) {
            var urlParams = "$url?"
            if (params != null) {
                for (param in params) {
                    urlParams = "$urlParams${param.key}=${param.value}&"
                }
            }
            urlParams = urlParams.substring(0, urlParams.length - 1)

            val queue = Volley.newRequestQueue(context)

            val stringRequest = JsonObjectRequest (
                method,
                urlParams,
                null,
                listener,
                errorListener
            )
            queue.add(stringRequest)
        }

        fun sendRequestString(
            context: Context?,
            method : Int,
            url : String,
            params : MutableMap<String, String>,
            listener: Response.Listener<String>,
            errorListener: Response.ErrorListener?,
        ) {
            var urlParams = "$url?"
            for (param in params) {
                urlParams = "$urlParams${param.key}=${param.value}&"
            }
            urlParams = urlParams.substring(0, urlParams.length - 1)

            val queue = Volley.newRequestQueue(context)

            val stringRequest = StringRequest(
                method,
                urlParams,
                listener,
                errorListener
            )
            queue.add(stringRequest)
        }

        fun sendRequestArray(
            context: Context?,
            method : Int,
            url : String,
            params : MutableMap<String, String>,
            listener: Response.Listener<JSONArray>,
            errorListener: Response.ErrorListener?,
        ) {
            var urlParams = "$url?"
            for (param in params) {
                urlParams = "$urlParams${param.key}=${param.value}&"
            }
            urlParams = urlParams.substring(0, urlParams.length - 1)

            val queue = Volley.newRequestQueue(context)

            val stringRequest = JsonArrayRequest(
                method,
                urlParams,
                null,
                listener,
                errorListener
            )
            queue.add(stringRequest)
        }
    }
}