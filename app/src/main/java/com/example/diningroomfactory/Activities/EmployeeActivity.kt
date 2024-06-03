package com.example.diningroomfactory.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.FirebaseStorageHelper
import com.example.diningroomfactory.Models.FactoryMenu
import com.example.diningroomfactory.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class EmployeeActivity : AppCompatActivity() {

    lateinit var menu_layout: ConstraintLayout
    lateinit var warehouse_layout: ConstraintLayout
    lateinit var order_layout: ConstraintLayout

    lateinit var menu_list:GridLayout
    lateinit var warehouse_list:LinearLayout
    lateinit var orders_list: LinearLayout

    lateinit var icon_menu_menu: ImageView
    lateinit var icon_menu_warehouse: ImageView
    lateinit var icon_menu_product: ImageView

    lateinit var tv_menu_menu : TextView
    lateinit var tv_menu_warehouse : TextView
    lateinit var tv_menu_product : TextView

    lateinit var reserve_products_button: Button
    lateinit var reserve_menus_button: Button

    lateinit var databaseHelper: DatabaseHelper
    lateinit var factoryId: String
    lateinit var prefs: SharedPreferences

    lateinit var type_products_button : Button
    lateinit var type_menu_button: Button


    var isReserverProducts = true
    var isTypeProduct = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_employee)

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        factoryId = prefs.getString("factoryId", "").toString()
        if(factoryId != ""){
            init()
        }
        else{
            Toast.makeText(this, "Не удалось получить индентификатор столовой, обратитесь к администратору", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun init(){
        databaseHelper = DatabaseHelper()

        type_products_button = findViewById(R.id.type_products_button)
        type_menu_button = findViewById(R.id.type_menu_button)

        menu_layout = findViewById(R.id.menu_layout)
        warehouse_layout = findViewById(R.id.warehouse_layout)
        order_layout = findViewById(R.id.order_layout)

        menu_list = findViewById(R.id.menu_list)
        warehouse_list = findViewById(R.id.warehouse_list)
        orders_list = findViewById(R.id.orders_list)

        icon_menu_menu = findViewById(R.id.icon_menu_menu)
        icon_menu_warehouse = findViewById(R.id.icon_menu_warehouse)
        icon_menu_product = findViewById(R.id.icon_menu_products)

        tv_menu_menu = findViewById(R.id.tv_menu_menu)
        tv_menu_warehouse = findViewById(R.id.tv_menu_warehouse)
        tv_menu_product = findViewById(R.id.tv_menu_products)

        reserve_menus_button = findViewById(R.id.reserve_menus_button)
        reserve_products_button = findViewById(R.id.reserve_products_button)

        ShowMenu()
    }

    fun ExitFromProfile(view: View){

        val editor = getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }


    //region All add methods

    val product_add_resultCode = 51
    val menu_add_resultCode = 52
    val reserve_products_add_resultCode = 53
    val reserve_menus_add_resultCode = 54

    fun AddWarehouse(view: View) {

        if(isReserverProducts){
            val intent = Intent(this@EmployeeActivity, ReserveProductActivity::class.java)
            intent.putExtra("factoryId", factoryId)
            startActivityForResult(intent, reserve_products_add_resultCode)
        }
        else{
            val intent = Intent(this@EmployeeActivity, ReserveMenuActivity::class.java)
            intent.putExtra("factoryId", factoryId)
            startActivityForResult(intent, reserve_menus_add_resultCode)
        }

    }

    fun AddMenu(view: View) {

        if(isTypeProduct == true){
            val intent = Intent(this@EmployeeActivity, ProductActivity::class.java)
            startActivityForResult(intent, product_add_resultCode)
        }
        else{
            val intent = Intent(this@EmployeeActivity, MenuActivity::class.java)
            intent.putExtra("isEdit", false)
            startActivityForResult(intent, menu_add_resultCode)
        }

    }

    //endregion


    //region All show methods

    fun ShowOrders(){
        menu_layout.visibility = View.GONE
        warehouse_layout.visibility = View.GONE
        order_layout.visibility = View.VISIBLE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product_blue))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.MidnightBlue))
        tv_menu_warehouse.setTextColor(getColor(R.color.black))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        LoadOrders()
    }

    fun ShowProducts() {
        isTypeProduct = true

        menu_layout.visibility = View.VISIBLE
        warehouse_layout.visibility = View.GONE
        order_layout.visibility = View.GONE

        type_products_button.setTextColor(getColor(R.color.MidnightBlue))
        type_menu_button.setTextColor(getColor(R.color.black))

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu_blue))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.black))
        tv_menu_menu.setTextColor(getColor(R.color.MidnightBlue))

        LoadProducts()
    }

    fun ShowMenu(){
        isTypeProduct = false

        menu_layout.visibility = View.VISIBLE
        warehouse_layout.visibility = View.GONE
        order_layout.visibility = View.GONE

        type_products_button.setTextColor(getColor(R.color.black))
        type_menu_button.setTextColor(getColor(R.color.MidnightBlue))

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu_blue))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.black))
        tv_menu_menu.setTextColor(getColor(R.color.MidnightBlue))

        LoadMenu()
    }

    fun ShowReserveProducts() {
        menu_layout.visibility = View.GONE
        warehouse_layout.visibility = View.VISIBLE
        order_layout.visibility = View.GONE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse_blue))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.MidnightBlue))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        reserve_products_button.setTextColor(getColor(R.color.MidnightBlue))
        reserve_menus_button.setTextColor(getColor(R.color.black))
        isReserverProducts = true
        LoadReserveProducts()

    }

    fun ShowReserveMenus() {
        menu_layout.visibility = View.GONE
        warehouse_layout.visibility = View.VISIBLE
        order_layout.visibility = View.GONE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse_blue))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.MidnightBlue))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        reserve_products_button.setTextColor(getColor(R.color.black))
        reserve_menus_button.setTextColor(getColor(R.color.MidnightBlue))
        isReserverProducts = false
        LoadReserveMenus()

    }

    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == product_add_resultCode){
            ShowProducts()
        }
        else if(requestCode == menu_add_resultCode){
            ShowMenu()
        }
        else if(requestCode == reserve_menus_add_resultCode){
            ShowReserveMenus()
        }
        else if(requestCode == reserve_products_add_resultCode){
            ShowReserveProducts()
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                Log.d("EmployeeActivity", "onActivityResult: ${selectedImageUri.path}")
                uploadImageToFirebase(selectedImageUri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val filePath = getRealPathFromURI(imageUri)
        FirebaseStorageHelper().uploadImage(filePath, { downloadUrl ->
            Log.d("EmployeeActivity", "uploadImageToFirebase: изображение загружено")
            databaseHelper.getProduct(productId){product ->
                if(product != null){
                    Log.d("EmployeeActivity", "uploadImageToFirebase: продукт найден")
                    product.photo = downloadUrl
                    Log.d("EmployeeActivity", "uploadImageToFirebase: ${product.photo}")
                    databaseHelper.updateProduct(productId, product){onComplete ->
                        if(onComplete){
                            Log.d("EmployeeActivity", "Продукт обновлен")
                            LoadProducts()
                            Toast.makeText(this, "Успешное изменение фото", Toast.LENGTH_SHORT).show()
                        }
                        else
                            Toast.makeText(this, "Не удалось изменить фото", Toast.LENGTH_SHORT).show()
                    }

                }
                else
                    Toast.makeText(this, "Не удалось изменить фото", Toast.LENGTH_SHORT).show()

            }

            Log.d("MenuActivity", "Image uploaded successfully: $downloadUrl")
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

    fun dpToPx(dp: Int) : Int{
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }

    fun isYesterdayOrLater(date: Date): Boolean {
        val calendar = Calendar.getInstance()

        // Получаем дату вчера
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, 0)
        yesterday.set(Calendar.HOUR_OF_DAY, 0)
        yesterday.set(Calendar.MINUTE, 0)
        yesterday.set(Calendar.SECOND, 0)
        yesterday.set(Calendar.MILLISECOND, 0)

        // Устанавливаем дату для сравнения
        calendar.time = date

        // Проверяем, что дата не раньше вчерашнего дня
        return !calendar.before(yesterday)
    }


    //region All loads methods

    fun LoadOrders(){
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        orders_list.removeAllViews()
        databaseHelper.getOrdersByFactoryId(factoryId){orders ->

            databaseHelper.getAllUsers { users ->

                databaseHelper.getAllMenu { menus ->

                    for(order in orders){

                        if(order.status == 0){

                            if(isYesterdayOrLater(order.date)){

                                val linearLayout = LinearLayout(this)
                                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                                linearLayout.orientation = LinearLayout.HORIZONTAL
                                linearLayout.background = getDrawable(R.drawable.background_line)
                                linearLayout.setPadding(0,0,0,dpToPx(10))
                                linearLayout.layoutParams = linearLayoutParams

                                val name = TextView(this)
                                val nameParams = LinearLayout.LayoutParams(dpToPx(200), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                                nameParams.setMargins(0, 0,0,0)
                                nameParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                                name.layoutParams = nameParams

                                val stringBuilder = StringBuilder()
                                stringBuilder.append("Заказ на ${dateFormat.format(order.date)} от" +
                                        " ${users.first { it.id == order.userId }!!.FIO}\nТабель: ${users.first { it.id == order.userId }!!.tabel}")

                                val spannableString = SpannableString(stringBuilder.toString())
                                spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                                name.setText(spannableString)
                                name.textSize = 16f
                                name.setTextColor(getColor(R.color.MidnightBlue))

                                val acceptButton = Button(this)
                                val acceptButtonParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50))
                                acceptButtonParams.setMargins(dpToPx(5), 0, dpToPx(5), 0)
                                acceptButtonParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                                acceptButton.layoutParams = acceptButtonParams
                                acceptButton.setText("ВЫДАТЬ")
                                acceptButton.textSize = 16f
                                acceptButton.setTextColor(getColor(R.color.MidnightBlue))
                                acceptButton.setOnClickListener {
                                    order.status = 1
                                    databaseHelper.updateOrder(order.id, order){}
                                    acceptButton.visibility = View.GONE
                                }

                                linearLayout.addView(name)
                                linearLayout.addView(acceptButton)

                                linearLayout.setOnClickListener{

                                    val intent = Intent(this@EmployeeActivity, OrderActivity::class.java)
                                    intent.putExtra("orderId", order.id)
                                    startActivity(intent)

                                }

                                orders_list.addView(linearLayout)

                            }

                        }
                    }

                }

            }

        }
    }

    fun LoadReserveMenus(){
        warehouse_list.removeAllViews()
        databaseHelper.getAllMenu { menus ->
            databaseHelper.getFactoryMenusByFactoryId(factoryId) { factoryMenus ->
                for(menu in menus){

                    val linearLayout = LinearLayout(this)
                    val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    linearLayout.background = getDrawable(R.drawable.background_line)
                    linearLayout.setPadding(0,0,0,dpToPx(10))
                    linearLayout.layoutParams = linearLayoutParams

                    val name = TextView(this)
                    val nameParams = LinearLayout.LayoutParams(dpToPx(100), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    nameParams.setMargins(0, 0,0,0)
                    nameParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    name.layoutParams = nameParams
                    name.setText(menu.name.toUpperCase())
                    name.textSize = 16f
                    name.setTextColor(getColor(R.color.MidnightBlue))

                    val count = EditText(this)
                    val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                    countParams.setMargins(0, 0,0,0)
                    countParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    count.background = getDrawable(R.drawable.background_shape)
                    count.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    count.layoutParams = countParams
                    count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                    count.setTextColor(getColor(R.color.MidnightBlue))
                    count.textSize = 16f
                    if(factoryMenus.any { it.menuId == menu.id }){
                        count.setText(factoryMenus.find { it.menuId == menu.id }!!.menuCount.toString())
                    }
                    else{
                        count.setText("0")
                    }
                    count.addTextChangedListener {

                        if(count.text.toString().isNotEmpty()){

                            if(count.text.toString().toIntOrNull() !== null) {

                                val men = factoryMenus.firstOrNull{it.menuId == menu.id}
                                if(men != null ){
                                    men.menuCount = count.text.toString().toInt()
                                    databaseHelper.updateFactoryMenu(men.id, men){
                                    }
                                }
                                else{
                                    databaseHelper.addFactoryMenu(FactoryMenu("0", factoryId, menu.id, count.text.toString().toInt())){
                                    }
                                }

                            }
                        }

                    }
                    count.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

                    linearLayout.addView(name)
                    linearLayout.addView(count)

                    warehouse_list.addView(linearLayout)

                }
            }
        }

    }

    fun LoadReserveProducts(){
        warehouse_list.removeAllViews()
        databaseHelper.getAllProducts { products ->
            databaseHelper.getFactoryProductsByFactoryId(factoryId) { factoryProducts ->

                for(product in products){

                    val linearLayout = LinearLayout(this)
                    val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    linearLayout.background = getDrawable(R.drawable.background_line)
                    linearLayout.setPadding(0,0,0,dpToPx(10))
                    linearLayout.layoutParams = linearLayoutParams

                    val name = TextView(this)
                    val nameParams = LinearLayout.LayoutParams(dpToPx(100), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    nameParams.setMargins(0, 0,0,0)
                    nameParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    name.layoutParams = nameParams
                    name.setText(product.name.toUpperCase())
                    name.textSize = 16f
                    name.setTextColor(getColor(R.color.MidnightBlue))

                    val count = TextView(this)
                    val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                    countParams.setMargins(0, 0,0,0)
                    countParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    count.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    count.layoutParams = countParams
                    count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                    count.setTextColor(getColor(R.color.MidnightBlue))
                    count.textSize = 16f
                    if(factoryProducts.any { it.productId == product.id }){
                        count.setText(factoryProducts.find { it.productId == product.id }!!.productCount.toString())
                    }
                    else{
                        count.setText("0")
                    }

                    linearLayout.addView(name)
                    linearLayout.addView(count)

                    warehouse_list.addView(linearLayout)

                }

            }
        }

    }

    fun LoadProducts(){

        val firebaseStorage = FirebaseStorageHelper()

        menu_list.removeAllViews()
        databaseHelper.getAllProducts {products ->

            var row = 0
            var column = 0

            for (product in products){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = GridLayout.LayoutParams()
                linearLayoutParams.width = getHalfScreenWidth() - dpToPx(30)
                linearLayoutParams.height =  dpToPx(150)
                linearLayoutParams.rowSpec = GridLayout.spec(row)
                linearLayoutParams.columnSpec = GridLayout.spec(column)
                linearLayoutParams.setMargins(dpToPx(10),dpToPx(20),dpToPx(10), 0)
                linearLayout.background = getDrawable(R.drawable.background_line)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val image = ImageView(this)
                val imageParams = LinearLayout.LayoutParams(getHalfScreenWidth() - dpToPx(30), dpToPx(100))
                image.layoutParams = imageParams
                image.setBackgroundResource(R.drawable.icon_apple)
                //region Выбор изображения продукта
                if(product.photo.isNotEmpty()) {
                    if(!isCheckPermission){
                        if(checkPermissions()){
                            val localFilePath = "${cacheDir}/${product.name}.jpg"
                            val localFile = File(localFilePath)

                                val imageUrl = product.photo
                                firebaseStorage.downloadImage(this, imageUrl, localFile,
                                    onSuccess = {
                                        setImageBackground(image, localFilePath)
                                    },
                                    onFailure = { exception ->
                                        Log.e("EmployeeActivity| LoadMenu ", "Failed to download image: ${exception.message}")
                                    }
                                )
                        }
                        else{
                            requestPermissions()
                        }
                    }
                    else{
                        image.setBackgroundResource(R.drawable.icon_menu_plug)
                    }
                }
                else{
                    image.setBackgroundResource(R.drawable.icon_menu_plug)
                }

                //endregion

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(0, dpToPx(10),0,0)
                nameParams.gravity = Gravity.CENTER_HORIZONTAL
                name.layoutParams = nameParams
                name.setText(product.name)
                name.textSize = 18f
                name.setTextColor(getColor(R.color.MidnightBlue))


                linearLayout.addView(image)
                linearLayout.addView(name)

                //region обработка нажатия на продукт
                linearLayout.setOnClickListener{
                    val options = arrayOf("Удалить", "Переименовать", "Сменить фото")

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Выберите действие")
                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                var flag = true
                                databaseHelper.getAllMenu { menus ->
                                    for(menu in menus){
                                        for(prod in menu.products){
                                            if(prod.productId == product.id){
                                                flag = false
                                            }
                                        }
                                    }
                                    if(flag)
                                    {
                                        // Действие "Удалить"
                                        databaseHelper.deleteProduct(product.id){complete ->
                                            if(complete){
                                                ShowProducts()
                                                Toast.makeText(this, "Успешное удаление", Toast.LENGTH_SHORT).show()
                                            }
                                            else{
                                                Toast.makeText(this, "Не удалось удалить", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    else{
                                        Toast.makeText(this, "Данный продукт используется в блюде(ах), нельзя удалить", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            1 -> {
                                // Действие "Переименовать"
                                val editText = EditText(this)
                                editText.setText(product.name)

                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Переименовать")
                                builder.setView(editText)
                                builder.setPositiveButton("OK") { dialog, which ->
                                    val newName = editText.text.toString()
                                    product.name = newName
                                    databaseHelper.updateProduct(product.id, product){ complete ->
                                        if(complete){
                                            ShowProducts()
                                            Toast.makeText(this, "Успешное изменение", Toast.LENGTH_SHORT).show()
                                        }
                                        else{
                                            Toast.makeText(this, "Не удалось изменить", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    dialog.dismiss()
                                }
                                builder.setNegativeButton("Отмена") { dialog, which ->
                                    dialog.dismiss()
                                }
                                builder.show()
                            }
                            2 -> {
                                productId = product.id
                                if(checkPermissions()){
                                        val intent = Intent(Intent.ACTION_PICK)
                                        intent.type = "image/*"
                                        startActivityForResult(intent, PICK_IMAGE_REQUEST)
                                }
                                else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        ActivityCompat.requestPermissions(
                                            this,
                                            arrayOf(
                                                Manifest.permission.READ_MEDIA_IMAGES
                                            ),
                                            REQUEST_CODE_STORAGE_PERMISSION_PRODUCT
                                        )
                                    }
                                    else{
                                        ActivityCompat.requestPermissions(
                                            this,
                                            arrayOf(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            ),
                                            REQUEST_CODE_STORAGE_PERMISSION_PRODUCT
                                        )
                                    }
                                }
                            }
                        }
                        dialog.dismiss()
                    }
                    builder.show()
                }

                //endregion

                menu_list.addView(linearLayout)

                // Перемещаемся к следующей ячейке
                column++
                if (column == 2) {
                    column = 0
                    row++
                }
            }

        }

    }
    private var productId: String = ""
    private val PICK_IMAGE_REQUEST = 1


    private fun getHalfScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        return screenWidth / 2
    }

    fun LoadMenu(){

        val firebaseStorage = FirebaseStorageHelper()

        menu_list.removeAllViews()
        databaseHelper.getAllMenu {menus ->

            var row = 0
            var column = 0

            for (menu in menus){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = GridLayout.LayoutParams()
                linearLayoutParams.width = getHalfScreenWidth() - dpToPx(30)
                linearLayoutParams.height =  dpToPx(150)
                linearLayoutParams.rowSpec = GridLayout.spec(row)
                linearLayoutParams.columnSpec = GridLayout.spec(column)
                linearLayoutParams.setMargins(dpToPx(10),dpToPx(20),dpToPx(10), 0)
                linearLayout.background = getDrawable(R.drawable.background_line)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.gravity = Gravity.CENTER_HORIZONTAL
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val image = ImageView(this)
                val imageParams = LinearLayout.LayoutParams(getHalfScreenWidth() - dpToPx(30), dpToPx(100))
                image.layoutParams = imageParams
                if(menu.photo.isNotEmpty()) {
                    if(!isCheckPermission){
                        if(checkPermissions()){
                            Log.d("Employee Activity", "Права есть");
                            val localFilePath = "${cacheDir}/${menu.name}.jpg"
                            val localFile = File(localFilePath)

                                // Если изображения нет на устройстве, загружаем его из Firebase Storage
                                val imageUrl = menu.photo // Получаем ссылку на изображение из menu.photo
                                firebaseStorage.downloadImage(this, imageUrl, localFile,
                                    onSuccess = {
                                        // Изображение успешно загружено, устанавливаем его фоном
                                        setImageBackground(image, localFilePath)
                                    },
                                    onFailure = { exception ->
                                        // Произошла ошибка при загрузке изображения
                                        Log.e("EmployeeActivity| LoadMenu ", "Failed to download image: ${exception.message}")
                                    }
                                )
                        }
                        else{
                            requestPermissions()
                        }
                    }
                    else{
                        image.setBackgroundResource(R.drawable.icon_menu_plug)
                    }
                }
                else{
                    image.setBackgroundResource(R.drawable.icon_menu_plug)
                }


                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(0, dpToPx(10),0,0)
                nameParams.gravity = Gravity.CENTER_HORIZONTAL
                name.layoutParams = nameParams
                name.setText(menu.name)
                name.textSize = 18f
                name.setTextColor(getColor(R.color.MidnightBlue))


                linearLayout.addView(image)
                linearLayout.addView(name)

                linearLayout.setOnClickListener{
                    val options = arrayOf("Удалить", "Редактировать")

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Выберите действие")
                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {

                                var flag = true
                                databaseHelper.getAllOrders { orders ->
                                    for(order in orders){
                                        for(m in order.menu){
                                            if(m.menuId == menu.id){
                                                flag = false
                                            }
                                        }
                                    }
                                    if(flag){
                                        databaseHelper.deleteMenu(menu.id){complete ->

                                            if(complete){
                                                ShowMenu()
                                                Toast.makeText(this, "Успешное удаление", Toast.LENGTH_SHORT).show()
                                            }
                                            else{
                                                Toast.makeText(this, "Не удалось удалить", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    else{
                                        Toast.makeText(this, "Данное блюдо есть в заказе, нельзя удалить", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            1 -> {
                                val intent = Intent(this@EmployeeActivity, MenuActivity::class.java)
                                intent.putExtra("isEdit", true)
                                intent.putExtra("id", menu.id)
                                startActivityForResult(intent, menu_add_resultCode)
                            }
                        }
                        dialog.dismiss()
                    }
                    builder.show()
                }

                menu_list.addView(linearLayout)

                // Перемещаемся к следующей ячейке
                column++
                if (column == 2) {
                    column = 0
                    row++
                }
            }

        }

    }

    //endregion

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 100
        private const val REQUEST_CODE_STORAGE_PERMISSION_PRODUCT = 102
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

            } else {
                isCheckPermission = true
                Toast.makeText(this, "Для работы некоторых функций требуются разрешения", Toast.LENGTH_SHORT).show()
            }
            LoadMenu()
        }
        else if(requestCode == REQUEST_CODE_STORAGE_PERMISSION_PRODUCT){
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            } else {
                Toast.makeText(this, "Для работы некоторых функций требуются разрешения", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun checkPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val readMediaImagesPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)

            if (readMediaImagesPermission != PackageManager.PERMISSION_GRANTED) {
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

    // Метод для установки изображения в качестве фона для ImageView
    private fun setImageBackground(imageView: ImageView, filePath: String) {
        val drawable = Drawable.createFromPath(filePath)
        imageView.background = drawable
    }

    //region All on click methods
    fun ShowOrdersButton(view: View) {
        ShowOrders()
    }

    fun ShowMenuButton(view: View) {
        ShowMenu()
    }

    fun ShowWarehouseButton(view: View) {
        ShowReserveMenus()
    }

    fun ShowReserveProductsButton(view: View) {
        ShowReserveProducts()
    }
    fun ShowReserveMenusButton(view: View) {
        ShowReserveMenus()
    }

    fun ShowTypeMenuButton(view: View) {
        ShowMenu()
    }
    fun ShowTypeProductsButton(view: View) {
        ShowProducts()
    }
    //endregion
}