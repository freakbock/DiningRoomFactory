package com.example.diningroomfactory

import android.content.Context
import android.util.Log
import com.example.diningroomfactory.Models.Factory
import com.example.diningroomfactory.Models.FactoryMenu
import com.example.diningroomfactory.Models.FactoryProduct
import com.example.diningroomfactory.Models.Menu
import com.example.diningroomfactory.Models.Order
import com.example.diningroomfactory.Models.Product
import com.example.diningroomfactory.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper  {

    private val TAG = "DatabaseHelper"

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val productRef: DatabaseReference by lazy {
        database.getReference("products")
    }
    private val ordersRef: DatabaseReference by lazy {
        database.getReference("orders")
    }
    private val factoryRef: DatabaseReference by lazy {
        database.getReference("factories")
    }
    private val factoryProductsRef: DatabaseReference by lazy{
        database.getReference("factoryProducts")
    }
    private val factoryMenusRef: DatabaseReference by lazy{
        database.getReference("factoryMenus")
    }
    private val userRef: DatabaseReference by lazy {
        database.getReference("users")
    }
    private val menuRef: DatabaseReference by lazy {
        database.getReference("menu")
    }


    //region Menu Data
    // Добавление данных
    fun addMenu(menu: Menu, onComplete: (Boolean) -> Unit){
        val menuId = menuRef.push().key ?: return
        menu.id = menuId
        menuRef.child(menuId).setValue(menu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    // Получение данных
    fun getMenu(menuId: String, callback: (Menu?) -> Unit) {
        menuRef.child(menuId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menu = snapshot.getValue(Menu::class.java)
                callback(menu)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Обновление данных
    fun updateMenu(menuId: String, menu: Menu, onComplete: (Boolean) -> Unit) {
        menuRef.child(menuId).setValue(menu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Удаление данных
    fun deleteMenu(menuId: String, onComplete: (Boolean) -> Unit) {
        menuRef.child(menuId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAllMenu(callback: (List<Menu>) -> Unit) {
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menus = mutableListOf<Menu>()
                for (menuSnapshot in snapshot.children) {
                    val menu = menuSnapshot.getValue(Menu::class.java)
                    menu?.let {
                        menus.add(it)
                    }
                }
                callback(menus)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }
    //endregion

    //region Product Data
    // Добавление данных
    fun addProduct(product: Product, onComplete: (Boolean) -> Unit){
        val productId = productRef.push().key ?: return
            product.id = productId
        productRef.child(productId).setValue(product)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    // Получение данных
    fun getProduct(productId: String, callback: (Product?) -> Unit) {
        productRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                callback(product)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Обновление данных
    fun updateProduct(productId: String, updatedProduct: Product, onComplete: (Boolean) -> Unit) {
        productRef.child(productId).setValue(updatedProduct)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Удаление данных
    fun deleteProduct(productId: String, onComplete: (Boolean) -> Unit) {
        productRef.child(productId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAllProducts(callback: (List<Product>) -> Unit) {
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        products.add(it)
                    }
                }
                callback(products)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }
    //endregion

    //region Factory Data
    fun addFactory(factory: Factory, onComplete: (Boolean) -> Unit) {
        val factoryId = factoryRef.push().key ?: return
        factory.id = factoryId
        factoryRef.child(factoryId).setValue(factory)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    fun getFactory(factoryId: String, callback: (Factory?) -> Unit) {
        factoryRef.child(factoryId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factory = snapshot.getValue(Factory::class.java)
                callback(factory)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(null)
            }
        })
    }

        fun updateFactory(factoryId: String, updatedFactory: Factory, onComplete: (Boolean) -> Unit) {
            factoryRef.child(factoryId).setValue(updatedFactory)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        }

    fun deleteFactory(factoryId: String, onComplete: (Boolean) -> Unit) {
        factoryRef.child(factoryId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAllFactories(callback: (List<Factory>) -> Unit) {
        factoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factories = mutableListOf<Factory>()
                for (factorySnapshot in snapshot.children) {
                    val factory = factorySnapshot.getValue(Factory::class.java)
                    factory?.let {
                        factories.add(it)
                    }
                }
                callback(factories)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }

    //endregion

    //region FactoryProduct Data
    // Добавление данных FactoryProduct
    fun addFactoryProduct(factoryProduct: FactoryProduct, onComplete: (Boolean) -> Unit) {
        val productId = factoryProductsRef.push().key ?: return
        factoryProduct.id = productId
        factoryProductsRef.child(productId).setValue(factoryProduct)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Получение данных FactoryProduct по идентификатору
    fun getFactoryProduct(factoryProductId: String, callback: (FactoryProduct?) -> Unit) {
        factoryProductsRef.child(factoryProductId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryProduct = snapshot.getValue(FactoryProduct::class.java)
                callback(factoryProduct)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Обновление данных FactoryProduct по идентификатору
    fun updateFactoryProduct(factoryProductId: String, factoryProduct: FactoryProduct, onComplete: (Boolean) -> Unit) {
        factoryProductsRef.child(factoryProductId).setValue(factoryProduct)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Удаление данных FactoryProduct по идентификатору
    fun deleteFactoryProduct(factoryProductId: String, onComplete: (Boolean) -> Unit) {
        factoryProductsRef.child(factoryProductId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Получение всех данных FactoryProduct
    fun getAllFactoryProducts(callback: (List<FactoryProduct>) -> Unit) {
        factoryProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryProducts = mutableListOf<FactoryProduct>()
                for (productSnapshot in snapshot.children) {
                    val factoryProduct = productSnapshot.getValue(FactoryProduct::class.java)
                    factoryProduct?.let {
                        factoryProducts.add(it)
                    }
                }
                callback(factoryProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }

    fun getFactoryProductsByFactoryId(desiredFactoryId: String, callback: (List<FactoryProduct>) -> Unit) {
        factoryProductsRef.orderByChild("factoryId").equalTo(desiredFactoryId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryProducts = mutableListOf<FactoryProduct>()
                for (productSnapshot in snapshot.children) {
                    val factoryProduct = productSnapshot.getValue(FactoryProduct::class.java)
                    factoryProduct?.let {
                        factoryProducts.add(it)
                    }
                }
                callback(factoryProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }
    //endregion

    //region FactoryMenu Data
    // Добавление данных
    fun addFactoryMenu(factoryMenu: FactoryMenu, onComplete: (Boolean) -> Unit){
        val menuId = factoryMenusRef.push().key ?: return
        factoryMenu.id = menuId
        factoryMenusRef.child(menuId).setValue(factoryMenu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    // Получение данных
    fun getFactoryMenu(factoryMenuId: String, callback: (FactoryMenu?) -> Unit) {
        factoryMenusRef.child(factoryMenuId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryMenu = snapshot.getValue(FactoryMenu::class.java)
                callback(factoryMenu)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Обновление данных
    fun updateFactoryMenu(factoryMenuId: String, factoryMenu: FactoryMenu, onComplete: (Boolean) -> Unit) {
        factoryMenusRef.child(factoryMenuId).setValue(factoryMenu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Удаление данных
    fun deleteFactoryMenu(factoryMenuId: String, onComplete: (Boolean) -> Unit) {
        factoryMenusRef.child(factoryMenuId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAllFactoryMenu(callback: (List<FactoryMenu>) -> Unit) {
        factoryMenusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryMenus = mutableListOf<FactoryMenu>()
                for (menuSnapshot in snapshot.children) {
                    val factoryMenu = menuSnapshot.getValue(FactoryMenu::class.java)
                    factoryMenu?.let {
                        factoryMenus.add(it)
                    }
                }
                callback(factoryMenus)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }

    fun getFactoryMenusByFactoryId(desiredFactoryId: String, callback: (List<FactoryMenu>) -> Unit) {
        factoryMenusRef.orderByChild("factoryId").equalTo(desiredFactoryId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val factoryMenus = mutableListOf<FactoryMenu>()
                for (menuSnapshot in snapshot.children) {
                    val factoryMenu = menuSnapshot.getValue(FactoryMenu::class.java)
                    factoryMenu?.let {
                        factoryMenus.add(it)
                    }
                }
                callback(factoryMenus)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }
    //endregion

    //region User Data

    fun getAllUsers(callback: (List<User>) -> Unit) {
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.child("id").getValue(String::class.java) ?: ""
                    val factoryId = userSnapshot.child("factoryId").getValue(String::class.java) ?: ""
                    val tabel = userSnapshot.child("tabel").getValue(String::class.java) ?: ""
                    val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                    val fio = userSnapshot.child("fio").getValue(String::class.java) ?: ""
                    val birthday = userSnapshot.child("birthday").getValue(String::class.java) ?: ""
                    val post = userSnapshot.child("post").getValue(String::class.java) ?: ""
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: ""

                    val user = User(id, factoryId, tabel, password, fio, birthday, post, role)
                    users.add(user)
                }
                callback(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: ${error.message}")
                callback(emptyList())
            }
        })
    }

    fun getUserByTabel(tabel: String, callback: (User?) -> Unit) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var foundUser: User? = null

                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.child("id").getValue()?.toString() ?: ""
                    val factoryId = userSnapshot.child("factoryId").getValue()?.toString() ?: ""
                    val userTabel = userSnapshot.child("tabel").getValue()?.toString() ?: ""
                    val password = userSnapshot.child("password").getValue()?.toString() ?: ""
                    val FIO = userSnapshot.child("fio").getValue()?.toString() ?: ""
                    val birthday = userSnapshot.child("birthday").getValue()?.toString() ?: ""
                    val post = userSnapshot.child("post").getValue()?.toString() ?: ""
                    val role = userSnapshot.child("role").getValue()?.toString() ?: ""

                    println("tabel_in $tabel")
                    println("tabel_out $userTabel")
                    // Проверяем условие нашедшегося пользователя
                    if (userTabel == tabel) {
                        println("НАШЁЛСЯ USER")
                        val user = User(id, factoryId, userTabel, password, FIO, birthday, post, role)
                        foundUser = user
                        break
                    }
                }
                callback(foundUser)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
                callback(null)
            }
        })
    }
    //endregion

    //region Orders Data

    fun getOrdersByFactoryId(factoryId: String, callback: (List<Order>) -> Unit) {
        val query = ordersRef.orderByChild("factoryId").equalTo(factoryId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        orders.add(it)
                    }
                }
                callback(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }


    fun addOrder(order: Order, onComplete: (Boolean) -> Unit) {
        val orderId = ordersRef.push().key
        if (orderId != null) {
            order.id = orderId
            ordersRef.child(orderId).setValue(order)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        } else {
            onComplete(false)
        }
    }

    fun getOrdersByUserId(userId: String, callback: (List<Order>) -> Unit) {
        val query = ordersRef.orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        orders.add(it)
                    }
                }
                callback(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getOrder(orderId: String, callback: (Order?) -> Unit) {
        ordersRef.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Order::class.java)
                callback(order)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Обновление данных заказа
    fun updateOrder(orderId: String, order: Order, onComplete: (Boolean) -> Unit) {
        ordersRef.child(orderId).setValue(order)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Удаление данных заказа
    fun deleteOrder(orderId: String, onComplete: (Boolean) -> Unit) {
        ordersRef.child(orderId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Получение всех заказов
    fun getAllOrders(callback: (List<Order>) -> Unit) {
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        orders.add(it)
                    }
                }
                callback(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }


    //endregion
}