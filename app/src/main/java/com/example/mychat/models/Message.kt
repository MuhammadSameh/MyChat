package com.example.mychat.models

import java.util.*

object MessageType{
    const val TEXT = 0
    const val IMAGE = 1
}
data class Message(
    val content: String = "",
    val senderId: String  = "",
    val time: Date? = null,
    val type: Int= MessageType.TEXT
) {
}