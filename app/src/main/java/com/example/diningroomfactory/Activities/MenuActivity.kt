package com.example.diningroomfactory.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.FirebaseStorageHelper
import com.example.diningroomfactory.Models.Menu
import com.example.diningroomfactory.Models.ProductPair
import com.example.diningroomfactory.R
import kotlin.math.roundToInt

class MenuActivity: AppCompatActivity() {

    private var productsForMenu: MutableList<ProductPair> = ArrayList()
    private lateinit var products_list: LinearLayout
    private lateinit var databaseHelper: DatabaseHelper
    lateinit var nameET: EditText
    lateinit var button: Button
    lateinit var photoTV: TextView

    private var isEdit = false
    private var id: String = ""
    private lateinit var menu: Menu
    private var photo: String = ""

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_add)
        nameET = findViewById(R.id.name)
        products_list = findViewById(R.id.menus_list)
        button = findViewById(R.id.button)
        photoTV = findViewById(R.id.photo)
        databaseHelper = DatabaseHelper()

        isEdit = intent.getBooleanExtra("isEdit", false)
        if(isEdit){
            id = intent.getStringExtra("id").toString()
            databaseHelper.getMenu(id){item ->
                if(item != null){
                    menu = item
                    photo = menu.photo
                    Log.d("MenuActivity", "onCreate: ${menu.photo}")
                    if(menu.photo.isNotEmpty()){
                        photoTV.setText("Есть фото")
                    }
                    nameET.setText(menu.name)
                    LoadProducts()
                }
            }
            button.setText("СОХРАНИТЬ")
            button.setOnClickListener {
                SaveMenu()
            }
        } else {
            button.setText("CОЗДАТЬ")
            button.setOnClickListener {
                CreateMenu()
            }
            LoadProducts()
        }
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }

    fun LoadProducts() {
        products_list.removeAllViews()

        databaseHelper.getAllProducts { products ->
            for(product in products){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayoutParams.setMargins(dpToPx(20), dpToPx(10), dpToPx(20), 0)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.setPadding(0, 0, 0, dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(dpToPx(100), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(0, 0, 0, 0)
                nameParams.gravity = Gravity.CENTER_VERTICAL
                name.layoutParams = nameParams
                name.text = product.name.toUpperCase()
                name.textSize = 16f
                name.setTextColor(getColor(R.color.MidnightBlue))

                val count = EditText(this)
                val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                countParams.setMargins(dpToPx(10), 0, 0, 0)
                countParams.gravity = Gravity.CENTER_VERTICAL
                if(isEdit){
                    menu.products.find { it.productId == product.id }?.let {
                        count.setText(it.number.toString())
                        productsForMenu.add(ProductPair(product.id, count.text.toString().toDouble()))
                    }
                }
                count.layoutParams = countParams
                count.setPadding(0, dpToPx(10), dpToPx(10), dpToPx(10))
                count.background = getDrawable(R.drawable.border_radius_input)
                count.textSize = 16f
                count.setTextColor(getColor(R.color.black))
                count.setHintTextColor(getColor(R.color.agate_gray))
                count.setHint("масса в кг")
                count.addTextChangedListener {
                    if(count.text.toString().isNotEmpty()){
                        if(count.text.toString() == "0"){
                            productsForMenu.find { it.productId == product.id }?.let {
                                productsForMenu.remove(it)
                            }
                        }
                        count.text.toString().toDoubleOrNull()?.let { countValue ->
                            productsForMenu.find { it.productId == product.id }?.let {
                                productsForMenu.remove(it)
                            }
                            productsForMenu.add(ProductPair(product.id, countValue))
                        }
                    } else {
                        productsForMenu.find { it.productId == product.id }?.let {
                            productsForMenu.remove(it)
                        }
                    }
                }
                count.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

                linearLayout.addView(name)
                linearLayout.addView(count)

                products_list.addView(linearLayout)
            }
        }
    }

    fun SaveMenu() {
        if(nameET.text.toString().isNotEmpty()){
            menu.name = nameET.text.toString()
            menu.products = productsForMenu
            menu.photo = photo
            databaseHelper.updateMenu(id, menu) { onComplete ->
                if(onComplete){
                    Toast.makeText(this, "Успешное редактирование пункта меню", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Не удалось отредактировать", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun CreateMenu() {
        val name = nameET.text.toString()
        if(name.isNotEmpty()){
            databaseHelper.addMenu(Menu("", name, productsForMenu, photo)) { onComplete ->
                if(onComplete){
                    Toast.makeText(this, "Успешное добавление нового пункта меню", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Не удалось добавить новый пункт меню", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun BackToMenu(view: View) {
        finish()
    }

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
            Log.d("MenuActivity", "Изображение успешно загружено: $photo")
            Toast.makeText(this, "Изображение успешно загружено", Toast.LENGTH_SHORT).show()
        }, { exception ->
            Log.e("MenuActivity", "Ошибка при загрузке изображения", exception)
            Toast.makeText(this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
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
