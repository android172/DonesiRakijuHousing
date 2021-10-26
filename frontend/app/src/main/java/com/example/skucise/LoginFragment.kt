package com.example.skucise

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.AuthFailureError
import com.android.volley.Request
import android.text.InputFilter

import android.text.Spanned

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.setOnClickListener{
            val usernameOrEmail = et_login_username_or_email.text.toString()
            val password = et_login_password.text.toString()

            if (usernameOrEmail.isBlank()) {
                reportError("Polje za Email/Korisničko ime je prazno!")
                return@setOnClickListener
            }
            if (password.isBlank()) {
                reportError("Polje za Šifru ime je prazno!")
                return@setOnClickListener
            }

            val url = "http://10.0.2.2:5000/api/login/user_login"

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
                        reportError("response:\n${response.toString()}")
                    }
                    catch (e: JSONException) {
                        reportError("json_error:\n$e")
                    }
                },
                { error ->
                    reportError("error:\n$error")
                }
            )
        }

        et_login_username_or_email.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        et_login_password.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
    }

    private fun reportError(message: String) {
        tv_login_unsuccessful.text = message
        tv_login_unsuccessful.visibility = View.VISIBLE
    }
}