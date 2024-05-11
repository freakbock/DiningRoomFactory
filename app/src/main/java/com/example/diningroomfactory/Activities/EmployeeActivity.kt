package com.example.diningroomfactory.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.R
import kotlin.math.roundToInt

class EmployeeActivity : AppCompatActivity() {

    lateinit var menu_layout: ConstraintLayout
    lateinit var product_layout: ConstraintLayout
    lateinit var warehouse_layout: ConstraintLayout

    lateinit var products_list: LinearLayout
    lateinit var menu_list:LinearLayout
    lateinit var warehouse_list:LinearLayout

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


    var isReserverProducts = true

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

        menu_layout = findViewById(R.id.menu_layout)
        product_layout = findViewById(R.id.product_layout)
        warehouse_layout = findViewById(R.id.warehouse_layout)

        products_list = findViewById(R.id.menus_list)
        menu_list = findViewById(R.id.menu_list)
        warehouse_list = findViewById(R.id.warehouse_list)

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

    //region All add methods

    val product_add_resultCode = 51
    val menu_add_resultCode = 52
    val reserve_products_add_resultCode = 53
    val reserve_menus_add_resultCode = 54

    fun AddProduct(view: View) {

        val intent = Intent(this@EmployeeActivity, ProductActivity::class.java)
        startActivityForResult(intent, product_add_resultCode)

    }

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

        val intent = Intent(this@EmployeeActivity, MenuActivity::class.java)
        intent.putExtra("isEdit", false)
        startActivityForResult(intent, menu_add_resultCode)

    }

    //endregion


    //region All show methods

    fun ShowProducts() {
        menu_layout.visibility = View.GONE
        product_layout.visibility = View.VISIBLE
        warehouse_layout.visibility = View.GONE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product_orange))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.orange))
        tv_menu_warehouse.setTextColor(getColor(R.color.black))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        LoadProducts()
    }

    fun ShowMenu(){
        menu_layout.visibility = View.VISIBLE
        product_layout.visibility = View.GONE
        warehouse_layout.visibility = View.GONE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu_orange))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.black))
        tv_menu_menu.setTextColor(getColor(R.color.orange))

        LoadMenu()
    }

    fun ShowReserveProducts() {
        menu_layout.visibility = View.GONE
        product_layout.visibility = View.GONE
        warehouse_layout.visibility = View.VISIBLE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse_orange))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.orange))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        reserve_products_button.setTextColor(getColor(R.color.orange))
        reserve_menus_button.setTextColor(getColor(R.color.black))
        isReserverProducts = true
        LoadReserveProducts()

    }

    fun ShowReserveMenus() {
        menu_layout.visibility = View.GONE
        product_layout.visibility = View.GONE
        warehouse_layout.visibility = View.VISIBLE

        icon_menu_product.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_product))
        icon_menu_warehouse.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_warehouse_orange))
        icon_menu_menu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.icon_menu))

        tv_menu_product.setTextColor(getColor(R.color.black))
        tv_menu_warehouse.setTextColor(getColor(R.color.orange))
        tv_menu_menu.setTextColor(getColor(R.color.black))

        reserve_products_button.setTextColor(getColor(R.color.black))
        reserve_menus_button.setTextColor(getColor(R.color.orange))
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
    }

    fun dpToPx(dp: Int) : Int{
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }


    //region All loads methods

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
                    name.setTextColor(getColor(R.color.orange))

                    val count = TextView(this)
                    val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                    countParams.setMargins(0, 0,0,0)
                    countParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    count.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    count.layoutParams = countParams
                    count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                    count.setTextColor(getColor(R.color.orange))
                    count.textSize = 16f
                    if(factoryMenus.any { it.menuId == menu.id }){
                        count.setText(factoryMenus.find { it.menuId == menu.id }!!.menuCount.toString())
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
                    name.setTextColor(getColor(R.color.orange))

                    val count = TextView(this)
                    val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                    countParams.setMargins(0, 0,0,0)
                    countParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    count.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    count.layoutParams = countParams
                    count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                    count.setTextColor(getColor(R.color.orange))
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

        products_list.removeAllViews()
        databaseHelper.getAllProducts {products ->

            for (product in products){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                linearLayout.background = getDrawable(R.drawable.background_line)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val image = ImageView(this)
                val imageParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(60))
                image.layoutParams = imageParams
                image.setBackgroundResource(R.drawable.icon_apple)

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(dpToPx(10), 0,0,0)
                nameParams.gravity = Gravity.CENTER_VERTICAL
                name.layoutParams = nameParams
                name.setText(product.name)
                name.textSize = 16f
                name.setTextColor(getColor(R.color.black))


                linearLayout.addView(image)
                linearLayout.addView(name)

                linearLayout.setOnClickListener{
                    val options = arrayOf("Удалить", "Переименовать")

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Выберите действие")
                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
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
                        }
                        dialog.dismiss()
                    }
                    builder.show()
                }

                products_list.addView(linearLayout)
            }

        }

    }

    fun LoadMenu(){

        menu_list.removeAllViews()
        databaseHelper.getAllMenu {menus ->

            for (menu in menus){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                linearLayout.background = getDrawable(R.drawable.background_line)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val image = ImageView(this)
                val imageParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(60))
                image.layoutParams = imageParams
                image.setBackgroundResource(R.drawable.icon_menu_plug)

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(dpToPx(5), 0,0,0)
                nameParams.gravity = Gravity.CENTER_VERTICAL
                name.layoutParams = nameParams
                name.setText(menu.name)
                name.textSize = 16f
                name.setTextColor(getColor(R.color.black))


                linearLayout.addView(image)
                linearLayout.addView(name)

                linearLayout.setOnClickListener{
                    val options = arrayOf("Удалить", "Редактировать")

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Выберите действие")
                    builder.setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                // Действие "Удалить"
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
            }

        }

    }


    //endregion


    //region All on click methods
    fun ShowProductsButton(view: View) {
        ShowProducts()
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
    //endregion
}