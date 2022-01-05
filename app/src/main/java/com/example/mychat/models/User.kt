package com.example.mychat.models

import java.io.Serializable

class User(
    var name: String = "",
    var Uid: String = "",
    var email: String = "",
    var picturePath: String = "default",
    var status: String = ""
): Serializable {
}