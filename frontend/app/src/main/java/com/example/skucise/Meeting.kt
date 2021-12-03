package com.example.skucise

import java.time.LocalDateTime

data class Meeting(
    val id: Int,
    val advertId: Int,
    val otherUser: Int,
    val username: String,
    val title: String,
    val proposedTime: LocalDateTime,
    val dateCreated: LocalDateTime,
    val agreedVisitor: Boolean,
    val agreedOwner: Boolean,
    val concluded: Boolean,
    val owner: Boolean
)
