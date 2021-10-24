package com.example.skucise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.AuthFailureError
import com.android.volley.Request


class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.setOnClickListener{

            val url = "http://10.0.2.2:5000/api/login/user_login"

            val usernameOrEmail = et_login_username_or_email.text.toString()
            val password = et_login_password.text.toString()

            val params = HashMap<String, String>()
            params["usernameOrEmail"] = usernameOrEmail
            params["password"] = password
            ReqSender.sendRequest(
                context = this.activity,
                Request.Method.GET,
                url,
                params,
                { response ->
                    try {
                        tv_login_unsuccessful.text = "response:\n${response.toString()}"
                        tv_login_unsuccessful.visibility = View.VISIBLE
                    }
                    catch (e: JSONException) {
                        tv_login_unsuccessful.text = "json_error:\n$e"
                        tv_login_unsuccessful.visibility = View.VISIBLE
                    }
                },
                { error ->
                    tv_login_unsuccessful.text = "error:\n$error"
                    tv_login_unsuccessful.visibility = View.VISIBLE
                }
            )
        }
    }
}