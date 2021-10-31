package com.example.skucise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.skucise.R
import com.example.skucise.fragments.FrontPageFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
    }

    override fun onStart() {
        super.onStart()
        nav_bottom_navigator.setupWithNavController(findNavController(R.id.frc_page_body))
    }
}