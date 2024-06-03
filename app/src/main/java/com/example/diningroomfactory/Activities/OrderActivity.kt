package com.example.diningroomfactory.Activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.Models.Order
import com.example.diningroomfactory.R
import java.text.SimpleDateFormat

class OrderActivity : AppCompatActivity() {

    private lateinit var name : TextView
    private lateinit var menus_list: LinearLayout
    private var orderId : String = ""
    private var order: Order = Order()

    private lateinit var databaseHelper : DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order)
        databaseHelper= DatabaseHelper()

        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy")

        menus_list = findViewById(R.id.menus_list)
        name = findViewById(R.id.name)
        orderId = intent.getStringExtra("orderId")!!
        databaseHelper.getOrder(orderId){order ->
            if (order != null){
                databaseHelper.getFactory(order.factoryId){factory ->
                    if(factory != null){
                        this.order = order
                        name.setText("Заказ на ${dateTimeFormat.format(order.date)} в столовой ${factory!!.factoryName}")

                        databaseHelper.getAllMenu { menus ->
                            menus_list.removeAllViews()
                            for(menu in order.menu){

                                val men = menus.find { it.id == menu.menuId }
                                if(men != null) {

                                    val parent = LinearLayout(this)
                                    val parentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150)
                                    parentParams.setMargins(20,20,20,20)
                                    parent.layoutParams = parentParams
                                    parent.setPadding(5,5,5,5)
                                    parent.orientation = LinearLayout.HORIZONTAL
                                    parent.background = getDrawable(R.drawable.background_shape)

                                    val name = TextView(this)
                                    val nameParams = LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    nameParams.setMargins(20,20,20,20)
                                    name.layoutParams = nameParams
                                    name.setText(men.name)
                                    name.setTextSize(16f)
                                    name.setTextColor(getColor(R.color.DeepSkyBlue))
                                    name.ellipsize = TextUtils.TruncateAt.END

                                    val count = TextView(this)
                                    val countParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    countParams.setMargins(40,20,20,20)
                                    count.layoutParams = countParams
                                    count.setText("в количестве " + menu.number.toString() + " шт.")
                                    count.setTextSize(16f)
                                    count.setTextColor(getColor(R.color.DeepSkyBlue))

                                    parent.addView(name)
                                    parent.addView(count)

                                    menus_list.addView(parent)

                                }

                            }
                        }
                    }
                }
            }
        }

    }

    fun BackToMenu(view: View) {

        finish()

    }
}