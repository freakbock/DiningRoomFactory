package com.example.diningroomfactory.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.FirebaseStorageHelper
import com.example.diningroomfactory.Models.Product
import com.example.diningroomfactory.R

class ProductActivity: AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var photo: String = ""
    lateinit var photoTV: TextView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_add)
        photoTV = findViewById(R.id.photo)

        databaseHelper = DatabaseHelper()
    }

    fun CreateProduct(view: View) {
        val nameET = findViewById<EditText>(R.id.name)
        val name = nameET.text.toString()
        if(name.isNotEmpty()){
            databaseHelper.addProduct(Product("",name, photo)){onComplete ->
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


    fun AddPhoto(view: View) {
        if(checkPermissions()){
            openGallery()
        }
        else{
            requestPermissions()
        }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 100
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri)
            }
        }
    }

    var isCheckPermission = false;
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                isCheckPermission = true
                Toast.makeText(this, "Для работы некоторых функций требуются разрешения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
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
                return false
            } else {
                return true
            }
        } else {
            // Для версий SDK ниже 33 запрашиваем только базовые разрешения
            if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
                return false
            } else {
                return true
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val filePath = getRealPathFromURI(imageUri)
        FirebaseStorageHelper().uploadImage(filePath, { downloadUrl ->
            photo = downloadUrl
            photoTV.setText("Фото выбрано")
            Log.d("MenuActivity", "Image uploaded successfully: $photo")
            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
        }, { exception ->
            Log.e("MenuActivity", "Error uploading image", exception)
            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show()
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var result: String = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            result = uri.path ?: ""
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx >= 0) {
                result = cursor.getString(idx)
            }
            cursor.close()
        }
        return result
    }
}