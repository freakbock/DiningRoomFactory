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
import com.example.diningroomfactory.Models.FactoryProduct
import com.example.diningroomfactory.R
import kotlin.math.roundToInt

class ReserveProductActivity : AppCompatActivity(){

    var factoryProducts: MutableList<FactoryProduct> = ArrayList()
    private lateinit var products_list: LinearLayout
    private lateinit var databaseHelper: DatabaseHelper
    lateinit var button: Button

    private var factoryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.reserve_product_add)
        products_list = findViewById(R.id.products_list)
        button = findViewById(R.id.button)
        databaseHelper = DatabaseHelper()

        factoryId = intent.getStringExtra("factoryId").toString()
        LoadProducts()

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
                count.layoutParams = countParams
                count.setPadding(0,dpToPx(10),dpToPx(10),dpToPx(10))
                count.background = getDrawable(R.drawable.border_radius_input)
                count.textSize = 16f
                count.setTextColor(getColor(R.color.black))
                count.setHintTextColor(getColor(R.color.agate_gray))
                count.setHint("масса в кг")
                count.addTextChangedListener{
                    if(count.text.toString().isNotEmpty()){
                        if(count.text.toString() == "0" || count.text.toString() == "0.0"){
                            if(factoryProducts.find{ it.productId == product.id} != null){
                                factoryProducts.remove(factoryProducts.find{ it.productId == product.id})
                            }
                        }
                        if(count.text.toString().toDoubleOrNull()!= null){
                            if(factoryProducts.find{ it.productId == product.id} == null){
                                factoryProducts.add(FactoryProduct("id", factoryId, product.id, count.text.toString().toDouble()))
                            }
                            else{
                                factoryProducts.remove(factoryProducts.find{ it.productId == product.id})
                                factoryProducts.add(FactoryProduct("id", factoryId, product.id, count.text.toString().toDouble()))
                            }
                        }
                    }
                    else{
                        if(factoryProducts.find{ it.productId == product.id} != null){
                            factoryProducts.remove(factoryProducts.find{ it.productId == product.id})
                        }
                    }
                }

                linearLayout.addView(name)
                linearLayout.addView(count)

                products_list.addView(linearLayout)
            }
        }
    }

    fun AddReserveProducts(view: View) {

        databaseHelper.getFactoryProductsByFactoryId(factoryId){ fps ->

            for(factoryProduct in factoryProducts){

                if(fps.any{it.productId == factoryProduct.productId}){

                    var fp = fps.find { it.productId == factoryProduct.productId }!!
                    fp.productCount = fp.productCount + factoryProduct.productCount
                    databaseHelper.updateFactoryProduct(fp.id, fp){
                        println("Продукт: " + fp.productId + ", в количестве " + factoryProduct.productCount + " БЫЛ ПОПОЛНЕН")
                    }
                }
                else{
                    databaseHelper.addFactoryProduct(factoryProduct){
                        println("Продукт: " + factoryProduct.productId + ", в количестве " + factoryProduct.productCount + " БЫЛ ВПЕРВЫЕ ДОБАВЛЕН")
                    }

                }

            }
            Toast.makeText(this, "Поставка продуктов успешно добавлена", Toast.LENGTH_SHORT).show()
            finish()

        }

    }

    fun BackToMenu(view: View) {finish()}

}