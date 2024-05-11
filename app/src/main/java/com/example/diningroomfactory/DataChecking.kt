package com.example.diningroomfactory

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DataChecking {

    fun isDateValid(text: String, dateFormat: String): Boolean{
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        sdf.isLenient = false

        return try{
            sdf.parse(text)
            true
        }
        catch (e: ParseException){
            false
        }
    }

}