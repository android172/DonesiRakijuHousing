package com.example.skucise

import android.annotation.SuppressLint
import java.time.LocalDate

@SuppressLint("NewApi")
data class User(
    var id : Int,
    var username : String = "",
    var password : String = "",
    var email : String = "",
    var firstname : String = "",
    var lastname : String = "",
    var creation_date : LocalDate = LocalDate.ofEpochDay(0)
)