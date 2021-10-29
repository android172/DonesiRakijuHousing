package com.example.skucise.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.skucise.R
import com.example.skucise.fragments.LoginFragment
import com.example.skucise.fragments.RegisterFragment
//import io.alterac.blurkit.BlurLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login_fragment.setOnClickListener {
            loginButtonClicked(LoginFragment())
        }
        btn_register_fragment.setOnClickListener {
            registerButtonClicked(RegisterFragment())
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

    private fun loginButtonClicked(fragment: Fragment){
        //change button color
        btn_login_fragment.background = ContextCompat.getDrawable(this,
            R.drawable.left_button_style_light
        )
        btn_register_fragment.background = ContextCompat.getDrawable(this,
            R.drawable.right_button_style_dark
        )
        changeActiveLRFragment(fragment)
    }

    private fun registerButtonClicked(fragment: Fragment){
        //change button color
        btn_login_fragment.background = ContextCompat.getDrawable(this,
            R.drawable.left_button_style_dark
        )
        btn_register_fragment.background = ContextCompat.getDrawable(this,
            R.drawable.right_button_style_light
        )
        changeActiveLRFragment(fragment)
    }
}