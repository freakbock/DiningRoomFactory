package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class FactoryProduct(

    @PropertyName("id")
    var id: String = "",

    @PropertyName("factoryId")
    var factoryId: String = "",

    @PropertyName("product")
    var productId: String = "",

    @PropertyName("productCount")
    var productCount: Int = 0

)