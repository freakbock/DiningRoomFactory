package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName
import java.util.Date

data class Order(

    @PropertyName("id")
    var id: String = "",

    @PropertyName("status")
    var status: Int = 0,

    @PropertyName("userId")
    var userId: String = "",

    @PropertyName("factoryId")
    var factoryId: String = "",

    @PropertyName("date")
    var date: Date = Date(),

    @PropertyName("menu")
    var menu: List<MenuPair> = ArrayList()

)