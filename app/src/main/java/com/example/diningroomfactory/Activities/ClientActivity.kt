package com.example.diningroomfactory.Activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Layout.Alignment
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.example.diningroomfactory.DatabaseHelper
import com.example.diningroomfactory.Models.FactoryMenu
import com.example.diningroomfactory.Models.MenuPair
import com.example.diningroomfactory.Models.Order
import com.example.diningroomfactory.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ClientActivity : AppCompatActivity() {

    lateinit var orders_layout : ConstraintLayout
    lateinit var menu_layout : ConstraintLayout
    lateinit var profile_layout : ConstraintLayout

    lateinit var orders_list : LinearLayout
    lateinit var menu_list : LinearLayout
    lateinit var profile_list : LinearLayout

    lateinit var icon_menu_orders : ImageView
    lateinit var icon_menu_menu : ImageView
    lateinit var icon_menu_profile : ImageView

    lateinit var tv_menu_orders : TextView
    lateinit var tv_menu_menu : TextView
    lateinit var tv_menu_profile : TextView

    lateinit var icon_addOrder: Button

    lateinit var databaseHelper: DatabaseHelper
    lateinit var id: String
    lateinit var tabel: String
    lateinit var prefs: SharedPreferences
    lateinit var factoryId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_client)

        databaseHelper = DatabaseHelper()

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        id = prefs.getString("id", "").toString()
        tabel = prefs.getString("tabel","").toString()

        init()
    }

    fun init(){

        orders_layout = findViewById(R.id.orders_layout)
        menu_layout = findViewById(R.id.menu_layout)
        profile_layout = findViewById(R.id.profile_layout)

        orders_list = findViewById(R.id.orders_list)
        menu_list = findViewById(R.id.menu_list)
        profile_list = findViewById(R.id.profile_list                          )

        icon_menu_orders = findViewById(R.id.icon_menu_orders)
        icon_menu_menu = findViewById(R.id.icon_menu_menu)
        icon_menu_profile = findViewById(R.id.icon_menu_profile)

        icon_addOrder = findViewById(R.id.icon_addOrder)

        tv_menu_orders = findViewById(R.id.tv_menu_orders)
        tv_menu_menu = findViewById(R.id.tv_menu_menu)
        tv_menu_profile = findViewById(R.id.tv_menu_profile)

        ShowMenu()
    }

    fun isToday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date

        val calendar2 = Calendar.getInstance()
        calendar2.time = Date()

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun filterOrdersByDate(orders: List<Order>, dateOrder: Date): List<Order> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDate = sdf.format(dateOrder)

        return orders.filter { sdf.format(it.date) == targetDate }
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

    var isHaveAllMenu = true
    fun AddOrder(view: View){
        if(menuInCart.size != 0){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Предупреждение")
                .setMessage("Создать заказ по выбранным товарам?")
                .setPositiveButton("ДА"){ dialog, _ ->

                    isHaveAllMenu = true

                    databaseHelper.getFactoryMenusByFactoryId(factoryId){factories ->

                        databaseHelper.getOrdersByFactoryId(factoryId){orders->

                            for(menInCart in menuInCart){

                                val menInFactory = factories.firstOrNull() { it.menuId == menInCart.menuId }!!

                                var menuCount = menInFactory.menuCount
                                for(order in filterOrdersByDate(orders, dateOrder)){
                                    for(orderMenu in order.menu){
                                        if(orderMenu.menuId == menInFactory.menuId){
                                            menuCount -= orderMenu.number
                                        }
                                    }
                                }

                                if(menInFactory != null){
                                    if(menuCount < menInCart.number)
                                    {
                                        isHaveAllMenu = false
                                    }
                                }
                                else{
                                    isHaveAllMenu = false
                                }
                            }

                            if(isHaveAllMenu){

                                databaseHelper.addOrder(Order("id", 0, id, factoryId, dateOrder,menuInCart)){onComplete ->
                                    if(onComplete){
                                        Toast.makeText(this, "Заказ успешно создан", Toast.LENGTH_SHORT).show()
                                        menuInCart.clear()
                                        ShowOrders()
                                    }
                                    else{
                                        Toast.makeText(this, "Не удалось создать заказ, попробуйте ещё раз", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(this, "В выбранной столовой не хватает блюд в наличии", Toast.LENGTH_SHORT).show()
                            }

//                            if(isToday(dateOrder)){

//                            }
//                            else{
//
//                                databaseHelper.addOrder(Order("id", 0, id, factoryId, dateOrder,menuInCart)){onComplete ->
//                                    if(onComplete){
//                                        Toast.makeText(this, "Заказ успешно создан", Toast.LENGTH_SHORT).show()
//                                        menuInCart.clear()
//                                        ShowOrders()
//                                    }
//                                    else{
//                                        Toast.makeText(this, "Не удалось создать заказ, попробуйте ещё раз", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//
//                            }

                        }
                    }

                }
                .setNegativeButton("НЕТ"){ dialog, _ ->

                    dialog.cancel()

                }
            val alert = builder.create()
            alert.show()
        }
        else{
            Toast.makeText(this, "Выберите товары для создания заказа", Toast.LENGTH_SHORT).show()
        }
    }



    //region Load Methods

    fun LoadFactories(){
        menu_list.removeAllViews()

        databaseHelper.getAllFactories {factories ->
            for(factory in factories){

                val linearLayout = LinearLayout(this)
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.background = getDrawable(R.drawable.background_shape)
                linearLayout.setPadding(0,0,0,dpToPx(10))
                linearLayout.layoutParams = linearLayoutParams

                val name = TextView(this)
                val nameParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                nameParams.setMargins(dpToPx(10), dpToPx(10),0,dpToPx(10))
                nameParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                name.layoutParams = nameParams
                name.setText(factory.factoryName.toUpperCase())
                name.textSize = 16f
                name.setTextColor(getColor(R.color.orange))

                linearLayout.addView(name)

                linearLayout.setOnClickListener {
                    factoryId = factory.id
                    LoadDates()
                }

                menu_list.addView(linearLayout)

            }
        }
    }

    fun LoadDates(){
        menu_list.removeAllViews()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (i in 0..6) {
            val date = calendar.time
            val dateString = dateFormat.format(date)

            val linearLayout = LinearLayout(this)
            val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.background = getDrawable(R.drawable.background_shape)
            linearLayout.setPadding(0,0,0,dpToPx(10))
            linearLayout.layoutParams = linearLayoutParams
            linearLayout.setOnClickListener{
                dateOrder = date
                LoadMenu()
            }

            val textView = TextView(this)
            val textViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(30))
            textViewParams.setMargins(dpToPx(0), dpToPx(15), dpToPx(0), dpToPx(10))
            textViewParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            textView.layoutParams = textViewParams
            textView.setTextColor(getColor(R.color.orange))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.setText(dateString)

            linearLayout.addView(textView)

            menu_list.addView(linearLayout)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }


    var menuInCart : MutableList<MenuPair> = ArrayList()
    var dateOrder = Date()
    fun LoadMenu(){

        icon_addOrder.visibility = View.VISIBLE
        menu_list.removeAllViews()

        databaseHelper.getFactoryMenusByFactoryId (factoryId){menus ->
            for(menu in menus){

                databaseHelper.getOrdersByFactoryId(factoryId){ orders ->


                    databaseHelper.getMenu(menu.menuId){men ->


                        val linearLayout = LinearLayout(this)
                        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        linearLayoutParams.setMargins(dpToPx(20),dpToPx(10),dpToPx(20), 0)
                        linearLayout.orientation = LinearLayout.HORIZONTAL
                        linearLayout.background = getDrawable(R.drawable.background_line)
                        linearLayout.setPadding(0,0,0,dpToPx(10))
                        linearLayout.layoutParams = linearLayoutParams

                        val name = TextView(this)
                        val nameParams = LinearLayout.LayoutParams(dpToPx(70), ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        nameParams.setMargins(0, 0,0,0)
                        nameParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        name.layoutParams = nameParams
                        name.setText(men!!.name)
                        name.textSize = 16f
                        name.setTextColor(getColor(R.color.orange))

                        val count = TextView(this)
                        val countParams = LinearLayout.LayoutParams(dpToPx(150), LinearLayout.LayoutParams.WRAP_CONTENT)
                        countParams.setMargins(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(10))
                        countParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        count.layoutParams = countParams

                        var menuCount = menu.menuCount
                        for(order in filterOrdersByDate(orders, dateOrder)){
                            for(orderMenu in order.menu){
                                if(orderMenu.menuId == menu.menuId){
                                    menuCount -= orderMenu.number
                                }
                            }
                        }

                        count.setText(menuCount.toString() + " шт. в наличии")
                        count.textSize = 16f
                        count.setTextColor(getColor(R.color.orange))

                        val countInCart = TextView(this)
                        val countInCartParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40))
                        countInCartParams.setMargins(dpToPx(0), dpToPx(15), dpToPx(0), dpToPx(10))
                        countInCartParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        countInCart.layoutParams = countInCartParams
                        countInCart.setText("0")
                        countInCart.setTextColor(getColor(R.color.orange))
                        countInCart.textAlignment = View.TEXT_ALIGNMENT_CENTER

                        val addButton = Button(this)
                        val addButtonParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40))
                        addButtonParams.setMargins(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(10))
                        addButtonParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        addButton.layoutParams = addButtonParams
                        addButton.setText("+")
                        addButton.setOnClickListener {

                            val menuCart = menuInCart.firstOrNull{it.menuId == menu.menuId}
                            if(menuCart != null){
                                    if(menuCount <= menuCart.number){
                                        Toast.makeText(this, "${men.name} кончился", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        menuInCart.firstOrNull{it.menuId == menu.menuId}!!.number ++
                                        countInCart.setText("${menuInCart.firstOrNull{it.menuId == menu.menuId}!!.number}")
                                    }
                            }
                            else {
                                menuInCart.add(MenuPair(menu.menuId, 1))
                                countInCart.setText("1")
                            }

                        }

                        val removeButton = Button(this)
                        val removeButtonParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40))
                        removeButtonParams.setMargins(dpToPx(0), dpToPx(10), dpToPx(0), dpToPx(10))
                        removeButtonParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        removeButton.layoutParams = removeButtonParams
                        removeButton.setText("-")
                        removeButton.setOnClickListener {

                            val menuCart = menuInCart.firstOrNull{it.menuId == menu.menuId}
                            if(menuCart != null){
                                if(menuCart.number == 1){
                                    menuInCart.remove(menuCart)
                                    countInCart.setText("0")
                                }
                                else{
                                    menuInCart.firstOrNull{it.menuId == menu.menuId}!!.number --
                                    countInCart.setText("${menuInCart.firstOrNull { it.menuId == menu.menuId }!!.number}")
                                }
                            }


                        }


                        linearLayout.addView(name)
                        linearLayout.addView(count)
                        linearLayout.addView(addButton)
                        linearLayout.addView(countInCart)
                        linearLayout.addView(removeButton)

                        menu_list.addView(linearLayout)

                    }


                }
            }
        }
    }

    fun LoadOrders(){
        orders_list.removeAllViews()

        databaseHelper.getOrdersByUserId(id){orders ->  
            for (order in orders){

                if(isYesterdayOrLater(order.date)){

                    databaseHelper.getFactory(order.factoryId){factory ->

                        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy")

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
                        name.setText("Заказ на ${dateTimeFormat.format(order.date)} в столовой ${factory!!.factoryName}")
                        name.textSize = 16f
                        name.setTextColor(getColor(R.color.orange))

                        val status = TextView(this)
                        val statusParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        statusParams.setMargins(0, 0,0,0)
                        statusParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        status.layoutParams = statusParams
                        if(order.status == 0){
                            status.setText("Статус: создан, ожидает получения")
                        }
                        else if(order.status == 1){
                            status.setText("Статус: Получен")
                        }
                        status.textSize = 16f
                        status.setTextColor(getColor(R.color.orange))


                        linearLayout.addView(name)
                        linearLayout.addView(status)

                        orders_list.addView(linearLayout)

                    }

                }
            }
        }
    }

    fun LoadProfile(){

        profile_list.removeAllViews()

        databaseHelper.getUserByTabel(tabel){user ->

            if(user != null){
                val FIO = TextView(this)
                val FIOParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(50))
                FIOParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                FIOParams.setMargins(dpToPx(10), dpToPx(30), 0 ,0)
                FIO.layoutParams = FIOParams
                FIO.setText("ФИО: " + user.FIO)
                FIO.textSize = 18f
                FIO.setTextColor(getColor(R.color.black))

                val birthday = TextView(this)
                val birthdayParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(50))
                birthdayParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                birthdayParams.setMargins(dpToPx(10), 0, 0 ,0)
                birthday.layoutParams = birthdayParams
                birthday.setText("Дата рождения: " + user.birthday)
                birthday.textSize = 18f
                birthday.setTextColor(getColor(R.color.black))

                val dolzhnost = TextView(this)
                val DolzhnostParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(50))
                DolzhnostParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                DolzhnostParams.setMargins(dpToPx(10), 0, 0 ,0)
                dolzhnost.layoutParams = DolzhnostParams
                dolzhnost.setText("Должность: " + user.post)
                dolzhnost.textSize = 18f
                dolzhnost.setTextColor(getColor(R.color.black))

                val tabel = TextView(this)
                val tabelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(50))
                tabelParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                tabelParams.setMargins(dpToPx(10), 0, 0 ,0)
                tabel.layoutParams = tabelParams
                tabel.setText("Табель: " + user.tabel)
                tabel.textSize = 18f
                tabel.setTextColor(getColor(R.color.black))

                profile_list.addView(FIO)
                profile_list.addView(birthday)
                profile_list.addView(dolzhnost)
                profile_list.addView(tabel)

            }
            else{
                val error = TextView(this)
                error.setText("Не удалось получить данные профиля")
                error.setTextSize(16f)
                error.setTextColor(getColor(R.color.black))
                profile_list.addView(error)
            }

        }
    }

    //endregion

    fun dpToPx(dp: Int) : Int{
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).roundToInt()
    }


    //region Show Methods

    fun ShowProfile(){

        menu_layout.visibility = View.GONE
        orders_layout.visibility = View.GONE
        profile_layout.visibility = View.VISIBLE

        icon_addOrder.visibility = View.GONE

        icon_menu_orders.setBackgroundResource(R.drawable.icon_order)
        icon_menu_menu.setBackgroundResource(R.drawable.icon_menu)
        icon_menu_profile.setBackgroundResource(R.drawable.icon_profile_orange)

        tv_menu_menu.setTextColor(getColor(R.color.black))
        tv_menu_orders.setTextColor(getColor(R.color.black))
        tv_menu_profile.setTextColor(getColor(R.color.orange))

        LoadProfile()

    }

    fun ShowMenu(){

        menu_layout.visibility = View.VISIBLE
        orders_layout.visibility = View.GONE
        profile_layout.visibility = View.GONE

        icon_addOrder.visibility = View.GONE

        icon_menu_orders.setBackgroundResource(R.drawable.icon_order)
        icon_menu_menu.setBackgroundResource(R.drawable.icon_menu_orange)
        icon_menu_profile.setBackgroundResource(R.drawable.icon_profile)

        tv_menu_menu.setTextColor(getColor(R.color.orange))
        tv_menu_orders.setTextColor(getColor(R.color.black))
        tv_menu_profile.setTextColor(getColor(R.color.black))

        LoadFactories()
    }

    fun ShowOrders(){

        menu_layout.visibility = View.GONE
        orders_layout.visibility = View.VISIBLE
        profile_layout.visibility = View.GONE

        icon_addOrder.visibility = View.GONE

        icon_menu_orders.setBackgroundResource(R.drawable.icon_order_orange)
        icon_menu_menu.setBackgroundResource(R.drawable.icon_menu)
        icon_menu_profile.setBackgroundResource(R.drawable.icon_profile)

        tv_menu_menu.setTextColor(getColor(R.color.black))
        tv_menu_orders.setTextColor(getColor(R.color.orange))
        tv_menu_profile.setTextColor(getColor(R.color.black))

        LoadOrders()
    }

    //endregion

    //region ShowButton Methods
    fun ShowMenuButton(view: View) {
        ShowMenu()
    }
    fun ShowOrdersButton(view: View) {
        ShowOrders()
    }

    fun ShowProfileButton(view: View) {
        ShowProfile()
    }

    //endregion
}