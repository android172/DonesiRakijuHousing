package com.example.skucise.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.skucise.*
import com.example.skucise.adapter.AccountDropdownAdapter
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Stop navigation menu from reloading same fragment
        nav_bottom_navigator.setOnItemReselectedListener {}

        // Dropdown toggle button
        btn_account_dd_toggle.setOnClickListener {
            if (drop_down_account.visibility == View.GONE) {
                drop_down_account.visibility = View.VISIBLE
            }
            else {
                drop_down_account.visibility = View.GONE
            }
        }

        // set account image
        btn_account_dd_toggle.clipToOutline = true
        if (SessionManager.currentUser != null)
            Glide.with(this)
                .load("http://10.0.2.2:5000/api/image/get_user_image_file?userId=${SessionManager.currentUser!!.id}")
                .centerCrop()
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .into(btn_account_dd_toggle)

        // List of dropdown options and their functionalities
        val dropdownOptions = mutableListOf<DropdownOption>()
        dropdownOptions.add(DropdownOption(
            "Moj nalog"
        ) {
            navigateToOutsideFragment(R.id.myAccountFragment)
            drop_down_account.visibility = View.GONE
        })
        dropdownOptions.add(DropdownOption(
            "Moji oglasi"
        ) {
            navigateToOutsideFragment(R.id.myAdvertsFragment)
            drop_down_account.visibility = View.GONE
        })
        dropdownOptions.add(DropdownOption(
            "Označeni oglasi"
        ) {
            nav_bottom_navigator.selectedItemId = nav_bottom_navigator.menu[2].itemId
            drop_down_account.visibility = View.GONE
        })
        dropdownOptions.add(DropdownOption(
            "Poruke"
        ) {
            nav_bottom_navigator.selectedItemId = nav_bottom_navigator.menu[3].itemId
            drop_down_account.visibility = View.GONE
        })
        dropdownOptions.add(DropdownOption(
            "Kalendar"
        ) {
            navigateToOutsideFragment(R.id.calendarFragment)
            drop_down_account.visibility = View.GONE
        })
        dropdownOptions.add(DropdownOption(
            "Odjavi se"
        ) {
            ReqSender.sendRequestString(
                this,
                Request.Method.POST,
                "http://10.0.2.2:5000/api/login/user_logout",
                null,
                {
                    SessionManager.stopSession()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                { error ->
                    SessionManager.stopSession()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Toast.makeText(this, "error:\n$error", Toast.LENGTH_LONG).show()
                }
            )
        })

        // Connecting dropdown recycler view necessities
        val accountDropdownAdapter = AccountDropdownAdapter(dropdownOptions)
        rcv_dd_options.adapter = accountDropdownAdapter
        rcv_dd_options.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        nav_bottom_navigator.setupWithNavController(findNavController(R.id.frc_page_body))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (drop_down_account.visibility != View.GONE) {
            val viewRect = Rect()
            drop_down_account.getGlobalVisibleRect(viewRect)
            if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
                drop_down_account.visibility = View.GONE
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun navigateToOutsideFragment(fragment: Int) {
        nav_bottom_navigator.menu.setGroupCheckable(0, true, false)
        for (i in 0 until nav_bottom_navigator.menu.size()) {
            nav_bottom_navigator.menu.getItem(i).isChecked = false
        }
        nav_bottom_navigator.menu.setGroupCheckable(0, true, true)

        findNavController(frc_page_body.id).navigate(fragment)
    }
}