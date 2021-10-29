package com.example.skucise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skucise.R
import com.example.skucise.fragments.FrontPageFragment

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_frontpage)

        // Set default fragment to homepage
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frc_page_body, FrontPageFragment())
    }
}