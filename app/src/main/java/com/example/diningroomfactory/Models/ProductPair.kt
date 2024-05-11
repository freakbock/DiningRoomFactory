package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class ProductPair (

    @PropertyName("product")
    var productId: String = "",
    @PropertyName("number")
    var number: Int = 0

)