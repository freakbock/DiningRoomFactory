package com.example.diningroomfactory.Activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.Models.Product
import com.example.diningroomfactory.R

class ProductActivity: AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_add)

        databaseHelper = DatabaseHelper()
    }

    fun CreateProduct(view: View) {
        val nameET = findViewById<EditText>(R.id.name)
        val name = nameET.text.toString()
        if(name.isNotEmpty()){
            databaseHelper.addProduct(Product("",name)){onComplete ->
                if(onComplete){
                    Toast.makeText(this, "Успешное добавление нового продукта", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this, "Не удалось добавить новый продукт", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun BackToMenu(view: View) {finish()}
}