package com.example.diningroomfactory.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.R
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper()
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(prefs.contains("tabel")){
            if(prefs.getString("role", "") == "0"){

            }
            else if(prefs.getString("role", "") == "1"){
                val intent = Intent(this, EmployeeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    fun BackToStart(view: View) {

        setContentView(R.layout.activity_main)

    }


    fun login(view: View) {
        try{
            val etTabel = findViewById<EditText>(R.id.etTabel)
            val etPassword = findViewById<EditText>(R.id.etPassword)

            if(etTabel.text.isNotEmpty() && etPassword.text.isNotEmpty()){

                if(etTabel.text.toString().toIntOrNull() != null){
                    if(etPassword.text.length >= 8 && etPassword.text.length <= 24){
                        databaseHelper.getUserByTabel(etTabel.text.toString()) { user ->
                            if (user != null) {
                                if(user.password == etPassword.text.toString()){
                                    val editor = prefs.edit()
                                    editor.putString("id", user.id)
                                    editor.putString("factoryId", user.factoryId)
                                    editor.putString("tabel", user.tabel)
                                    editor.putString("role", user.role)
                                    editor.apply()

                                    if(user.role == "0"){

                                    }
                                    else if(user.role == "1"){

                                        val intent = Intent(this, EmployeeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                                else
                                    Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(
                                    this,
                                    "Аккаунт с введенным табельным не существует",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                    else{
                        Toast.makeText(this, "Минимальное количество символов пароля - 8", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, "Табельный номер должен быть числом!", Toast.LENGTH_SHORT).show()
                }
            }else
                Toast.makeText(this, "Введите данные для входа", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun goLogin(view: View) {
        setContentView(R.layout.login)
    }
}