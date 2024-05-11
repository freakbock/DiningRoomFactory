package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class User(
    @PropertyName("id")
    var id: String = "",

    @PropertyName("factoryId")
    var factoryId: String = "0",

    @PropertyName("tabel")
    var tabel: String = "0",

    @PropertyName("password")
    var password: String = "12345678",

    @PropertyName("FIO")
    var FIO: String = "Ivanov Ivan Ivanovich",

    @PropertyName("birthday")
    var birthday: String = "01.01.2001",

    @PropertyName("post")
    var post: String = "Sotrudnik",

    @PropertyName("role")
    var role: String = "0"
)
