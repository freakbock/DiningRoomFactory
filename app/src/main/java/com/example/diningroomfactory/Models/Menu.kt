package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class Menu(
    @PropertyName("id")
    var id: String = "",
    @PropertyName("name")
    var name: String = "",
    @PropertyName("products")
    var products: MutableList<ProductPair> = ArrayList()
)
 