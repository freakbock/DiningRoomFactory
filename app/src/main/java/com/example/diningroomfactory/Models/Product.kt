package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class Product(
    @PropertyName("id")
    var id: String = "",
    @PropertyName("name")
    var name: String = "",
    @PropertyName("photo")
    var photo: String = ""
)
