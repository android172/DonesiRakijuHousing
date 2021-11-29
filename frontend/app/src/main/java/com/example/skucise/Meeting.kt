package com.example.skucise

import java.time.LocalDateTime

data class Meeting(
    val id: Int,
    val title: String,
    val username: String,
    val proposedTime: LocalDateTime,
    val dateCreated: LocalDateTime
)
