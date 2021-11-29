package com.example.skucise.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.example.skucise.R
import com.example.skucise.ReqSender
import com.example.skucise.User
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : Fragment() {

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ReqSender.sendRequest(
            requireContext(),
            Request.Method.POST,
            "http://10.0.2.2:5000/api/users/get_my_info",
            null,
            { response ->

                user = User (
                    id              = response.getInt("id"),
                    username        = response.getString("username"),
                    firstname       = response.getString("firstName"),
                    lastname        = response.getString("lastName"),
                    email           = response.getString("email"),
                    creationDate    = LocalDateTime.parse(response.getString("dateCreated")),
                    numberOfAdverts = response.getInt("numberOfAdverts"),
                    averageRating   = response.getString("userScore")
                )

                loadPageData()
            } ,
            { error ->
                Toast.makeText(context, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPageData() {
        if (user == null) return

        // Update text
        et_user_username.setText(user!!.username)
        et_user_firstname.setText(user!!.firstname)
        et_user_lastname.setText(user!!.lastname)
        tv_user_number_of_adverts.text = "Objavljenih oglasa: ${user!!.numberOfAdverts}"
        tv_user_average_rating.text = user!!.averageRating
        et_user_date_created.setText(user!!.creationDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))
        et_user_email.setText(user!!.email)

        // Update profile picture
        img_user_pfp.clipToOutline = true
        Glide.with(this)
            .load("http://10.0.2.2:5000/api/image/get_user_image_file?userId=${user!!.id}")
            .centerCrop()
            .into(img_user_pfp)

        // Setup edit user
        val defaultUsername = user!!.username
        btn_user_edit_username.setOnClickListener {
            et_user_username.isEnabled = true
            btn_user_edit_username.isEnabled = false
            btn_user_edit_cancel.isEnabled = true
            btn_user_edit_confirm.isEnabled = true
        }
        val defaultFirstname = user!!.firstname
        btn_user_edit_firstname.setOnClickListener {
            et_user_firstname.isEnabled = true
            btn_user_edit_firstname.isEnabled = false
            btn_user_edit_cancel.isEnabled = true
            btn_user_edit_confirm.isEnabled = true
        }
        val defaultLastname = user!!.lastname
        btn_user_edit_lastname.setOnClickListener {
            et_user_lastname.isEnabled = true
            btn_user_edit_lastname.isEnabled = false
            btn_user_edit_cancel.isEnabled = true
            btn_user_edit_confirm.isEnabled = true
        }

        btn_user_edit_cancel.setOnClickListener {
            et_user_username.setText(defaultUsername)
            et_user_firstname.setText(defaultFirstname)
            et_user_lastname.setText(defaultLastname)
            et_user_username.isEnabled = false
            et_user_firstname.isEnabled = false
            et_user_lastname.isEnabled = false
            btn_user_edit_username.isEnabled = true
            btn_user_edit_firstname.isEnabled = true
            btn_user_edit_lastname.isEnabled = true
            btn_user_edit_cancel.isEnabled = false
            btn_user_edit_confirm.isEnabled = false
        }
        btn_user_edit_confirm.setOnClickListener {
            val params: HashMap<String, String> = HashMap()
            params["newUsername"]  = et_user_username.text.toString()
            params["newFirstName"] = et_user_firstname.text.toString()
            params["newLastName"]  = et_user_lastname.text.toString()

            ReqSender.sendRequestString(
                requireContext(),
                Request.Method.POST,
                "http://10.0.2.2:5000/api/users/change_user_info",
                params,
                {
                    et_user_username.isEnabled = false
                    et_user_firstname.isEnabled = false
                    et_user_lastname.isEnabled = false
                    btn_user_edit_username.isEnabled = true
                    btn_user_edit_firstname.isEnabled = true
                    btn_user_edit_lastname.isEnabled = true
                    btn_user_edit_cancel.isEnabled = false
                    btn_user_edit_confirm.isEnabled = false
                } ,
                { error ->
                    Toast.makeText(context, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MyAccountFragment.
         */
        @JvmStatic
        fun newInstance() =
            MyAccountFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}