package com.example.diningroomfactory.Models

import com.google.firebase.database.PropertyName

data class Factory(
    @PropertyName("id")
    var id: String = "",
    @PropertyName("factoryName")
    var factoryName: String = ""
)
