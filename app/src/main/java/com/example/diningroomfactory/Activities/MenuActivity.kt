package com.example.diningroomfactory.Activities

import android.os.Bundle
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
import androidx.core.widget.addTextChangedListener
import com.example.diningroomfactory.DatabaseHelper
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

    private var isEdit = false
    private var id: String = ""
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_add)
        nameET = findViewById(R.id.name)
        products_list = findViewById(R.id.menus_list)
        button = findViewById(R.id.button)
        databaseHelper = DatabaseHelper()


        isEdit = intent.getBooleanExtra("isEdit", false)
        if(isEdit){
            id = intent.getStringExtra("id").toString()
            databaseHelper.getMenu(id){item ->
                if(item !=null){
                    menu = item
                    nameET.setText(menu.name)
                    LoadProducts()
                } }
            button.setText("СОХРАНИТЬ")
            button.setOnClickListener {
                SaveMenu()
            }
        }
        else{
            button.setText("CОЗДАТЬ")
            button.setOnClickListener {
                CreateMenu()
            }
            LoadProducts()
        }
    }

    fun dpToPx(dp: Int) : Int{
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }



    fun LoadProducts(){
        products_list.removeAllViews()

        databaseHelper.getAllProducts { products ->
            for(product in products){
                val linearLayout = LinearLayout(this)
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(dpToPx(100), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(0, 0,0,0)
                nameParams.gravity = Gravity.CENTER_VERTICAL
                name.layoutParams = nameParams
                name.setText(product.name.toUpperCase())
                name.textSize = 16f
                name.setTextColor(getColor(R.color.orange))

                val count = EditText(this)
                val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                countParams.setMargins(dpToPx(10), 0,0,0)
                countParams.gravity = Gravity.CENTER_VERTICAL
                if(isEdit){
                    if(menu.products.find { it.productId == product.id } != null){
                        count.setText(menu.products.find{it.productId == product.id}!!.number.toString())
                        productsForMenu.add(ProductPair(product.id, count.text.toString().toDouble()))
                    }
                }
                count.layoutParams = countParams
                count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                count.background = getDrawable(R.drawable.border_radius_input)
                count.textSize = 16f
                count.setTextColor(getColor(R.color.black))
                count.setHintTextColor(getColor(R.color.agate_gray))
                count.setHint("масса в кг")
                count.addTextChangedListener{
                    if(count.text.toString().isNotEmpty()){
                        if(count.text.toString() == "0"){
                            if(productsForMenu.find{ it.productId == product.id} != null){
                                productsForMenu.remove(productsForMenu.find{ it.productId == product.id})
                            }
                        }
                        if(count.text.toString().toDoubleOrNull()!= null){
                            if(productsForMenu.find{ it.productId == product.id} == null){
                                productsForMenu.add(ProductPair(product.id, count.text.toString().toDouble()))
                            }
                            else{
                                productsForMenu.remove(productsForMenu.find{ it.productId == product.id})
                                productsForMenu.add(ProductPair(product.id, count.text.toString().toDouble()))
                            }
                        }
                    }
                    else{
                        if(productsForMenu.find{ it.productId == product.id} != null){
                            productsForMenu.remove(productsForMenu.find{ it.productId == product.id})
                        }
                    }
                }

                linearLayout.addView(name)
                linearLayout.addView(count)

                products_list.addView(linearLayout)
            }
        }
    }
    fun SaveMenu(){
        if(nameET.text.toString().isNotEmpty()){
            menu.name = nameET.text.toString()
            menu.products = productsForMenu
            databaseHelper.updateMenu(id, menu){onComplete ->
                if(onComplete){
                    Toast.makeText(this, "Успешное редактирование пункта меню", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this, "Не удалось отредактировать", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun CreateMenu() {
        val name = nameET.text.toString()
        if(name.isNotEmpty()){
            databaseHelper.addMenu(Menu("",name, productsForMenu)){ onComplete ->
                if(onComplete){
                    Toast.makeText(this, "Успешное добавление нового пункта меню", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this, "Не удалось добавить новый пункт меню", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun BackToMenu(view: View) {finish()}
}