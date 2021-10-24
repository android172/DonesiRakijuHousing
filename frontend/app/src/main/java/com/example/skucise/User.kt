package com.example.skucise

import java.util.*

data class User(
    var id : Int,
    var username : String,
    var password : String,
    var email : String,
    var firstname : String,
    var lastname : String,
    var creation_date : Date
)