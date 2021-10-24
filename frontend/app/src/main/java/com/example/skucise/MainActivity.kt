package com.example.skucise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login_fragment.setOnClickListener {
            changeActiveLRFragment(LoginFragment())
        }
        btn_register_fragment.setOnClickListener {
            changeActiveLRFragment(RegisterFragment())
        }

        // Set default fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frc_login_or_register, LoginFragment())
            .commit()

    }

    private fun changeActiveLRFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frc_login_or_register, fragment)
        fragmentTransaction.commit()
    }
}