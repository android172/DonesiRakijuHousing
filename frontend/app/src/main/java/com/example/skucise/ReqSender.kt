package com.example.skucise

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject


class ReqSender {
    companion object {

        private val queuedRequests : HashMap<Context, RequestQueue> = HashMap()

        private fun getRequestQueue(context: Context): RequestQueue {
            if (!queuedRequests.containsKey(context)) {
                queuedRequests[context] = Volley.newRequestQueue(context)
            }
            return queuedRequests[context]!!
        }

        private fun buildUrl(base_url: String, params: MutableMap<String, String>?): String {
            var url = "$base_url?"
            if (params != null) {
                for (param in params) {
                    url = "$url${param.key}=${param.value}&"
                }
            }
            return url.substring(0, url.length - 1)
        }



        fun sendRequest(
            context: Context,
            method : Int,
            url : String,
            params : MutableMap<String, String>?,
            listener: Response.Listener<JSONObject>,
            errorListener: Response.ErrorListener?,
            authorization: Boolean = true
        ) {
            val fullUrl = buildUrl(url, params)
            val queue = getRequestQueue(context)
            val stringRequest = object : JsonObjectRequest (
                method,
                fullUrl,
                null,
                listener,
                errorListener
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    if (!authorization) return super.getHeaders()
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${SessionManager.token.toString()}"
                    return headers
                }
            }
            queue.add(stringRequest)
        }

        fun sendRequestString(
            context: Context,
            method : Int,
            url : String,
            params : MutableMap<String, String>?,
            listener: Response.Listener<String>,
            errorListener: Response.ErrorListener?,
            authorization: Boolean = true
        ) {
            val fullUrl = buildUrl(url, params)
            val queue = getRequestQueue(context)
            val stringRequest = object : StringRequest(
                method,
                fullUrl,
                listener,
                errorListener
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    if (!authorization) return super.getHeaders()
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${SessionManager.token.toString()}"
                    return headers
                }
            }
            queue.add(stringRequest)
        }

        fun sendRequestArray(
            context: Context,
            method : Int,
            url : String,
            params : MutableMap<String, String>?,
            listener: Response.Listener<JSONArray>,
            errorListener: Response.ErrorListener?,
            authorization: Boolean = true
        ) {
            val fullUrl = buildUrl(url, params)
            val queue = getRequestQueue(context)
            val request = object : JsonArrayRequest(
                method,
                fullUrl,
                null,
                listener,
                errorListener
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    if (!authorization) return super.getHeaders()
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${SessionManager.token.toString()}"
                    return headers
                }
            }
            queue.add(request)
        }
    }
}