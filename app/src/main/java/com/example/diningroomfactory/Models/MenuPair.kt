package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class MenuPair(
    @PropertyName("menu")
    var menuId: String = "",
    @PropertyName("number")
    var number: Int = 0
)