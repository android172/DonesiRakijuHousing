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
import com.example.skucise.*
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        var x = "yes"
        if (SessionManager.sharedPreferences == null) x = "no"
        Toast.makeText(this, x, Toast.LENGTH_LONG).show()

        // Dropdown toggle button
        btn_account_dd_toggle.setOnClickListener {
            if (drop_down_account.visibility == View.GONE) {
                drop_down_account.visibility = View.VISIBLE
            }
            else {
                drop_down_account.visibility = View.GONE
            }
        }

        // List of dropdown options and their functionalities
        val dropdownOptions = mutableListOf<DropdownOption>()
        dropdownOptions.add(DropdownOption(
            "Moj nalog"
        ) {})
        dropdownOptions.add(DropdownOption(
            "Moji oglasi"
        ) {})
        dropdownOptions.add(DropdownOption(
            "Označeni oglasi"
        ) {
            nav_bottom_navigator.selectedItemId = nav_bottom_navigator.menu[2].itemId
        })
        dropdownOptions.add(DropdownOption(
            "Poruke"
        ) {
            nav_bottom_navigator.selectedItemId = nav_bottom_navigator.menu[3].itemId
        })
        dropdownOptions.add(DropdownOption(
            "Kalendar"
        ) {})
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
}