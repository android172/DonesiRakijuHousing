package com.example.skucise.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skucise.AccountDropdownAdapter
import com.example.skucise.DropdownOption
import com.example.skucise.R
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

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
        ) {})

        // Connecting dropdown recycler view necessities
        val accountDropdownAdapter = AccountDropdownAdapter(dropdownOptions)
        rcv_dd_options.adapter = accountDropdownAdapter
        rcv_dd_options.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        nav_bottom_navigator.setupWithNavController(findNavController(R.id.frc_page_body))
    }
}