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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.Models.FactoryMenu
import com.example.diningroomfactory.Models.FactoryProduct
import com.example.diningroomfactory.R
import kotlin.math.roundToInt

class ReserveMenuActivity : AppCompatActivity() {

    var factoryMenus: MutableList<FactoryMenu> = ArrayList()
    private lateinit var menus_list: LinearLayout
    private lateinit var databaseHelper: DatabaseHelper

    private var factoryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.reserve_menu_add)
        menus_list = findViewById(R.id.menus_list)
        databaseHelper = DatabaseHelper()

        factoryId = intent.getStringExtra("factoryId").toString()
        LoadMenus()
    }


    fun dpToPx(dp: Int) : Int{
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }

    fun LoadMenus(){
        menus_list.removeAllViews()

        databaseHelper.getAllMenu { menus ->
            for(menu in menus){
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
                name.setText(menu.name.toUpperCase())
                name.textSize = 16f
                name.setTextColor(getColor(R.color.orange))

                val count = EditText(this)
                val countParams = LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, dpToPx(40))
                countParams.setMargins(dpToPx(10), 0,0,0)
                countParams.gravity = Gravity.CENTER_VERTICAL
                count.layoutParams = countParams
                count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                count.background = getDrawable(R.drawable.border_radius_input)
                count.textSize = 16f
                count.setTextColor(getColor(R.color.black))
                count.setHintTextColor(getColor(R.color.agate_gray))
                count.setHint("количество порций")
                count.addTextChangedListener{
                    if(count.text.toString().isNotEmpty()){
                        if(count.text.toString() == "0"){
                            if(factoryMenus.find{it.menuId == menu.id} != null){
                                factoryMenus.remove(factoryMenus.find{ it.menuId == menu.id})
                            }
                        }
                        else if(count.text.toString().toIntOrNull()!= null){
                            if(factoryMenus.find{it.menuId == menu.id} == null){
                                factoryMenus.add(FactoryMenu("id", factoryId, menu.id, count.text.toString().toInt()))
                            }
                            else{
                                factoryMenus.remove(factoryMenus.find{ it.menuId == menu.id})
                                factoryMenus.add(FactoryMenu("id", factoryId, menu.id, count.text.toString().toInt()))
                            }
                        }
                    }
                    else{
                        if(factoryMenus.find{it.menuId == menu.id} != null){
                            factoryMenus.remove(factoryMenus.find{ it.menuId == menu.id})
                        }
                    }
                }

                linearLayout.addView(name)
                linearLayout.addView(count)

                menus_list.addView(linearLayout)
            }
        }
    }

    var isLack = true
    var isCompleted = true

    fun AddReserveMenus(view: View) {
        isLack = true
        isCompleted = true

        val factoryProducts = mutableListOf<FactoryProduct>()

        databaseHelper.getAllMenu { menus ->

            for (factoryMenuItem in factoryMenus) {
                val menu = menus.find { it.id == factoryMenuItem.menuId }
                if (menu != null) {
                    val products = menu.products

                    for (productPair in products) {

                        val spentProductCount = productPair.number * factoryMenuItem.menuCount
                        val existingFactoryProduct =
                            factoryProducts.find { it.productId == productPair.productId }
                        if (existingFactoryProduct != null) {
                            println("НЕ РАВЕН NULL")
                            existingFactoryProduct.productCount += spentProductCount

                        } else {
                            println("РАВЕН NULL")

                            factoryProducts.add(
                                FactoryProduct(
                                    "id",
                                    factoryId,
                                    productPair.productId,
                                    spentProductCount
                                )
                            )
                        }
                    }
                }
            }

            println("количество видов продуктов необходимых ${factoryProducts.size}")

            databaseHelper.getFactoryProductsByFactoryId(factoryId) { fps ->
                for (factoryProduct in factoryProducts) {
                    val fp = fps.find { it.productId == factoryProduct.productId }
                    if (fp != null) {
                        factoryProduct.productCount = fp.productCount - factoryProduct.productCount
                    }

                    if (factoryProduct.productCount < 0) {
                        isLack = false
                        println("Необходим продукт ${factoryProduct.productId} в размере ${factoryProduct.productCount}")
                    }
                }

                CheckLack(factoryProducts)
            }
        }


    }

    fun CheckLack(factoryProducts: List<FactoryProduct>){
        if(isLack){
            databaseHelper.getFactoryProductsByFactoryId(factoryId){ fps ->
                for(factoryProduct in factoryProducts){
                    val fp = fps.find { it.productId == factoryProduct.productId }
                    if(fp != null)
                    {
                        fp.productCount = factoryProduct.productCount
                        databaseHelper.updateFactoryProduct(fp.id, fp){}
                    }
                }
            }
            finish()

//            databaseHelper.getFactoryMenusByFactoryId(factoryId){ fms ->
//                for(factoryMenu in factoryMenus)      {
//                    val fm = fms.find { it.menuId == factoryMenu.menuId }
//                    if(fm != null){
//                        fm.menuCount += factoryMenu.menuCount
//                        databaseHelper.updateFactoryMenu(fm.id, fm){}
//                    }
//                    else{
//                        databaseHelper.addFactoryMenu(factoryMenu){}
//                    }
//                }
//                finish()
//            }
        }
        else{
            val stringBuilder = StringBuilder()
            stringBuilder.append("Не хватает:\n")
            databaseHelper.getAllProducts { products ->
                for(factoryProduct in factoryProducts){
                    if(factoryProduct.productCount < 0){
                        val product = products.find { it.id == factoryProduct.productId }
                        if(product != null){
                            stringBuilder.append("${product.name} в размере ${factoryProduct.productCount} шт.\n")
                        }
                    }
                }

                showAlertDialog(stringBuilder.toString())
            }

        }
    }

    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ПРЕДУПРЕЖДЕНИЕ")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
            }
        builder.create().show()
    }

    fun BackToMenu(view: View) {finish()}


}