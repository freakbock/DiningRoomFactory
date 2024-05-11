package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class FactoryMenu (

    @PropertyName("id")
    var id: String = "",
    @PropertyName("factoryId")
    var factoryId: String = "",
    @PropertyName("menu")
    var menuId: String = "",
    @PropertyName("menuCount")
    var menuCount: Int = 0

)