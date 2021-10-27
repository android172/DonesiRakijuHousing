package com.example.skucise

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.fragment.app.Fragment
import com.android.volley.Request
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONException

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register button
        btn_register.setOnClickListener {
            val firstname = et_register_first_name.text.toString()
            val lastname = et_register_last_name.text.toString()
            val email = et_register_email.text.toString()
            val username = et_register_username.text.toString()
            val password = et_register_password.text.toString()
            val repeatedPassword = et_register_repeated_password.text.toString()

            // Check if all inputs are correct
            if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || username.isBlank() ||
                password.isBlank() || repeatedPassword.isBlank()) {
                reportError("Sva polja moraju biti popunjena.")
                return@setOnClickListener
            }

            if (!Regex("^([ \\u00c0-\\u01ffa-zA-Z'\\-])+\$").matches(firstname)) {
                reportError("Ime i prezime moraju biti slova abecede (ili alfabeta).")
                return@setOnClickListener
            }
            if (!Regex("^([ \\u00c0-\\u01ffa-zA-Z'\\-])+\$").matches(lastname)) {
                reportError("Ime i prezime moraju biti slova abecede (ili alfabeta).")
                return@setOnClickListener
            }

            if (!Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$").matches(email)) {
                reportError("Forma email adrese je pogrešna.")
                return@setOnClickListener
            }

            if (!Regex("^[A-Za-z0-9_-]{4,16}$").matches(username)) {
                reportError("Korisničko ime mora sadržati od 4 do 16 karaktera, i to samo slova abecede, brojeve, donju crtu ili crticu.")
                return@setOnClickListener
            }

            if (repeatedPassword != password) {
                reportError("Šifra nije dobro ponovljena.")
                return@setOnClickListener
            }

            if (!Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,32}$").matches(password)) {
                reportError("Šifra mora da sadrži makar jedno malo i veliko slovo. broj, i svega od 8 do 32 karaktera")
                return@setOnClickListener
            }

            // Send request
            val url = "http://10.0.2.2:5000/api/login/user_register"

            val params = HashMap<String, String>()
            params["firstName"] = firstname
            params["lastName"] = lastname
            params["email"] = email
            params["username"] = username
            params["password"] = password

            ReqSender.sendRequestString(
                context = this.activity,
                method = Request.Method.POST,
                url = url,
                params = params,
                listener = { response ->
                    try {
                        reportError("response:\n${response.toString()}")
                    }
                    catch (e: JSONException) {
                        reportError("json_error:\n$e")
                    }
                },
                errorListener = { error ->
                    reportError("error:\n$error")
                }
            )
        }

        // Disable white spaces as inputs
        et_register_username.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        et_register_email.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        et_register_password.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        et_register_repeated_password.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
    }

    private fun reportError(message: String) {
        tv_register_unsuccessful.text = message
        tv_register_unsuccessful.visibility = View.VISIBLE
    }
}