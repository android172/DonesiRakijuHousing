package com.example.skucise.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import com.android.volley.Request
import android.text.InputFilter
import com.example.skucise.activities.NavigationActivity
import com.example.skucise.R
import com.example.skucise.ReqSender
import com.example.skucise.SessionManager
import com.example.skucise.Util

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var errorReport : Util.Companion.ErrorReport

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorReport = Util.Companion.ErrorReport(tv_login_unsuccessful)

        btn_login.setOnClickListener{
            val usernameOrEmail = et_login_username_or_email.text.toString()
            val password = et_login_password.text.toString()

            if (usernameOrEmail.isBlank()) {
                errorReport.reportError("Polje za Email/Korisničko ime je prazno!")
                return@setOnClickListener
            }
            if (password.isBlank()) {
                errorReport.reportError("Polje za Šifru ime je prazno!")
                return@setOnClickListener
            }

            val url = "http://10.0.2.2:5000/api/login/user_login"

            val params = HashMap<String, String>()
            params["usernameOrEmail"] = usernameOrEmail
            params["password"] = password
            ReqSender.sendRequest(
                context = this.activity,
                Request.Method.POST,
                url,
                params,
                { response ->
                    try {
                        val token = response.getString("token")
                        SessionManager.startSession(token)

                        startActivity(Intent(this.activity, NavigationActivity::class.java))
                        this.activity?.finish()
                    } catch (e: JSONException) {
                        errorReport.reportError("json_error:\n$e")
                    }
                },
                { error ->
                    errorReport.reportError("error:\n$error")
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
}