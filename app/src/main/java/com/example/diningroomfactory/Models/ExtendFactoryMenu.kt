package com.example.diningroomfactory.Models

import android.view.Menu
import com.google.firebase.database.PropertyName
import java.util.Date

data class ExtendFactoryMenu(
    @PropertyName("id")
    var id: String = "",

    @PropertyName("factoryId")
    var factoryId: String = "",

    @PropertyName("menuId")
    var menuId : String = "",

    @PropertyName("count")
    var count : Int = 0,

    @PropertyName("date")
    var date: Date = Date()
)
