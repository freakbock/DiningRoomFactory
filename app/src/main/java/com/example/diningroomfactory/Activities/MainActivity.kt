package com.example.diningroomfactory.Activities

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.R
class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper()

        checkStoragePermissions()
    }

    private fun Init(){
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(prefs.contains("tabel")){
            if(prefs.getString("role", "") == "0"){
                val intent = Intent(this, ClientActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if(prefs.getString("role", "") == "1"){
                val intent = Intent(this, EmployeeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 100
    }

    private fun checkStoragePermissions() {

        // Проверка разрешения на чтение из памяти
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        // Проверка разрешения на запись в память
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 10 (API 29) и выше нужно также проверить READ_MEDIA_IMAGES
            val readMediaImagesPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)

            if (readPermission != PackageManager.PERMISSION_GRANTED ||
                writePermission != PackageManager.PERMISSION_GRANTED ||
                readMediaImagesPermission != PackageManager.PERMISSION_GRANTED) {
                // Запрос разрешений, если они не предоставлены
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    ),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                Init()
            }
        } else {
            // Для версий SDK ниже 33 запрашиваем только базовые разрешения
            if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
                // Запрос разрешений, если они не предоставлены
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                // Разрешения уже предоставлены, выполняем необходимые действия
                Init()
            }
        }
    }

    // Обработка результата запроса разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

            } else {
                // Разрешения не предоставлены, выводим сообщение об ошибке
                Toast.makeText(this, "Для работы некоторых функций требуются разрешения", Toast.LENGTH_SHORT).show()
            }
            Init()
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
                                        val intent = Intent(this, ClientActivity::class.java)
                                        startActivity(intent)
                                        finish()
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