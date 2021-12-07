package com.example.skucise.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.skucise.*
import com.example.skucise.Util.Companion.getFileExtension
import com.example.skucise.Util.Companion.getFileName
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : Fragment() {

    private var user: User? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    val loadImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data

            val inputStream = requireActivity().contentResolver.openInputStream(uri!!)
            val contents = Base64.getEncoder().encodeToString(inputStream!!.readBytes())

            val image = FileData(
                Name = requireContext().getFileName(uri),
                Extension = requireContext().getFileExtension(uri)!!,
                Content = contents
            )

            ReqSender.sendImage(
                requireContext(),
                Request.Method.PUT,
                "http://10.0.2.2:5000/api/image/set_user_image",
                image,
                {
                    Glide.with(requireContext())
                        .load(uri)
                        .centerCrop()
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .into(img_user_pfp)

                    val accountPfp = requireActivity().btn_account_dd_toggle
                    accountPfp.clipToOutline = true
                    if (SessionManager.currentUser != null)
                        Glide.with(requireContext())
                            .load(uri)
                            .centerCrop()
                            .signature(ObjectKey(System.currentTimeMillis().toString()))
                            .into(accountPfp)
                },
                { error ->
                    Toast.makeText(requireContext(), "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

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

        tv_profile_username.text = user!!.username
        val fullname = user!!.firstname + " " + user!!.lastname
        tv_profile_name.text = fullname
        tv_profile_email.text = user!!.email
        val date = "" + user!!.creationDate.dayOfMonth + "." + user!!.creationDate.monthValue + "." + user!!.creationDate.year
        tv_profile_date.text = date
        tv_profile_number_adverts.text = user!!.numberOfAdverts.toString()
        tv_average_rating.text = user!!.averageRating

        // Update profile picture
        img_user_pfp.clipToOutline = true
        Glide.with(this)
            .load("http://10.0.2.2:5000/api/image/get_user_image_file?userId=${user!!.id}")
            .centerCrop()
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(img_user_pfp)

        // Change profile picture
        btn_edit_profile_picture.setOnClickListener {
            galleryCheckPermission()
        }
        btn_edit_user.setOnClickListener {
            csl_profile_edit_container.visibility = View.VISIBLE
        }
        btn_end_editing.setOnClickListener {
            csl_profile_edit_container.visibility = View.GONE
        }

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
                    if (et_user_username.isEnabled) {
                        val newUsername = et_user_username.text.toString()
                        SessionManager.changeUsername(newUsername)
                        requireActivity().tv_account_dd_username.text = newUsername
                    }

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

    private fun galleryCheckPermission() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    loadImageFromGallery.launch(intent)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        requireContext(),
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT
                    ).show()
                    showRotationalDialogForPermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }

            }).onSameThread().check()
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("Dozvola za pristup neophodna.")
            .setPositiveButton("Podešavanja") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
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