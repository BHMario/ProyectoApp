package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "mangup.db"
        // v1: schema | v2: stock | v3: image_uri | v4: fix taza image
        const val DATABASE_VERSION = 4

        // ---- Tables ----
        private const val TABLE_USERS = "users"
        private const val TABLE_PRODUCTS = "products"
        private const val TABLE_ORDERS = "orders"
        private const val TABLE_ORDER_ITEMS = "order_items"
    }

    // ─────────────────────────────── Lifecycle ────────────────────────────── //

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users table
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT    NOT NULL,
                email       TEXT    UNIQUE NOT NULL,
                phone       TEXT,
                password    TEXT    NOT NULL,
                is_admin    INTEGER DEFAULT 0
            )
        """.trimIndent())

        // Products table
        db.execSQL("""
            CREATE TABLE $TABLE_PRODUCTS (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT    NOT NULL,
                price       REAL    NOT NULL,
                category    TEXT    NOT NULL,
                description TEXT,
                stock       INTEGER DEFAULT 0,
                image_uri   TEXT
            )
        """.trimIndent())

        // Orders table — 1:N relation with users
        db.execSQL("""
            CREATE TABLE $TABLE_ORDERS (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id     INTEGER NOT NULL,
                date        TEXT    NOT NULL,
                status      TEXT    NOT NULL DEFAULT 'En proceso',
                total       REAL    NOT NULL,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id)
            )
        """.trimIndent())

        // Order items table — N:M relation between orders and products
        db.execSQL("""
            CREATE TABLE $TABLE_ORDER_ITEMS (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id    INTEGER NOT NULL,
                product_id  INTEGER NOT NULL,
                quantity    INTEGER NOT NULL,
                unit_price  REAL    NOT NULL,
                FOREIGN KEY (order_id)   REFERENCES $TABLE_ORDERS(id),
                FOREIGN KEY (product_id) REFERENCES $TABLE_PRODUCTS(id)
            )
        """.trimIndent())

        seedProducts(db)
        seedAdminUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_PRODUCTS ADD COLUMN stock INTEGER DEFAULT 0")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_PRODUCTS ADD COLUMN image_uri TEXT")
            // Actualizar datos existentes con las rutas de assets si es necesario
            seedProductsImages(db)
        }
        if (oldVersion < 4) {
            // Corregir imagen de Taza Ataque a los Titanes
            db.execSQL(
                "UPDATE $TABLE_PRODUCTS SET image_uri = 'assets://images/taza_ataque_a_los_titanes.png' WHERE name = 'Taza Ataque a los Titanes'"
            )
        }
    }

    private fun seedProductsImages(db: SQLiteDatabase) {
        // Mapeo simple para actualizar productos existentes por nombre
        val updates = mapOf(
            "Manga Jujutsu Kaisen Vol 1" to "assets://images/jujutsu_kaisen_vol1.png",
            "Figura Akatsuki"            to "assets://images/figura_akatsuki.png",
            "Camiseta Naruto"             to "assets://images/camiseta_naruto.png",
            "Manga One Piece Vol 5"       to "assets://images/one_piece_vol5.png",
            "Figura Demon Slayer"         to "assets://images/figura_demon_slayer.png",
            "Mochila Anime"               to "assets://images/mochila_anime.png",
            "Manga My Hero Vol 3"         to "assets://images/my_hero_vol3.png",
            "Gorro Sailor Moon"           to "assets://images/gorro_sailor_moon.png",
            "Manga Dragon Ball Vol 1"     to "assets://images/jujutsu_kaisen_vol1.png", // Usando JJK como temporal si no hay DB1
            "Figura Gojo Satoru"          to "assets://images/jujutsu_kaisen_vol1.png",
            "Taza Ataque a los Titanes"   to "assets://images/taza_ataque_a_los_titanes.png",
            "Manga Bleach Vol 2"          to "assets://images/jujutsu_kaisen_vol1.png"
        )
        updates.forEach { (name, uri) ->
            db.execSQL("UPDATE $TABLE_PRODUCTS SET image_uri = ? WHERE name = ?", arrayOf(uri, name))
        }
    }

    // ───────────────────────────── Seed data ──────────────────────────────── //

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun seedAdminUser(db: SQLiteDatabase) {
        db.insert(TABLE_USERS, null, ContentValues().apply {
            put("name", "Admin MangUP")
            put("email", "admin@mangup.com")
            put("phone", "")
            put("password", hashPassword("admin123"))
            put("is_admin", 1)
        })
    }

    private fun seedProducts(db: SQLiteDatabase) {
        val rows = listOf(
            listOf("Manga Jujutsu Kaisen Vol 1", 12.99, "Manga",         "Primer volumen de Jujutsu Kaisen",        15, "assets://images/jujutsu_kaisen_vol1.png"),
            listOf("Figura Akatsuki",            24.99, "Figuras",        "Figura articulada Akatsuki",              8,  "assets://images/figura_akatsuki.png"),
            listOf("Camiseta Naruto",             18.99, "Merchandising", "Camiseta 100% algodón diseño Naruto",     20, "assets://images/camiseta_naruto.png"),
            listOf("Manga One Piece Vol 5",       13.99, "Manga",         "Quinto volumen de One Piece",             10, "assets://images/one_piece_vol5.png"),
            listOf("Figura Demon Slayer",         29.99, "Figuras",        "Figura premium de Tanjiro en acción",     5,  "assets://images/figura_demon_slayer.png"),
            listOf("Mochila Anime",               32.99, "Merchandising", "Mochila con diseños exclusivos anime",    12, "assets://images/mochila_anime.png"),
            listOf("Manga My Hero Vol 3",         12.99, "Manga",         "Tercer volumen de My Hero Academia",      18, "assets://images/my_hero_vol3.png"),
            listOf("Gorro Sailor Moon",           16.99, "Merchandising", "Gorro de invierno con logo Sailor Moon",  7,  "assets://images/gorro_sailor_moon.png"),
            listOf("Manga Dragon Ball Vol 1",     11.99, "Manga",         "El inicio de la aventura de Goku",        14, "assets://images/jujutsu_kaisen_vol1.png"),
            listOf("Figura Gojo Satoru",          34.99, "Figuras",        "Figura premium de Gojo con efectos",      4,  "assets://images/jujutsu_kaisen_vol1.png"),
            listOf("Taza Ataque a los Titanes",    9.99, "Merchandising", "Taza cerámica diseño exclusivo",          25, "assets://images/taza_ataque_a_los_titanes.png"),
            listOf("Manga Bleach Vol 2",          12.99, "Manga",         "Segundo volumen de Bleach",               11, "assets://images/jujutsu_kaisen_vol1.png")
        )
        rows.forEach { r ->
            db.insert(TABLE_PRODUCTS, null, ContentValues().apply {
                put("name",        r[0] as String)
                put("price",       r[1] as Double)
                put("category",    r[2] as String)
                put("description", r[3] as String)
                put("stock",       r[4] as Int)
                put("image_uri",   r[5] as String)
            })
        }
    }

    // ─────────────────────────────── USERS ────────────────────────────────── //

    fun registerUser(name: String, email: String, phone: String, password: String): Boolean {
        return try {
            writableDatabase.insert(TABLE_USERS, null, ContentValues().apply {
                put("name",     name)
                put("email",    email.lowercase().trim())
                put("phone",    phone)
                put("password", hashPassword(password))
                put("is_admin", 0)
            }) != -1L
        } catch (e: Exception) {
            false
        }
    }

    fun updateUserName(userId: Int, name: String): Boolean =
        writableDatabase.update(
            TABLE_USERS,
            ContentValues().apply { put("name", name) },
            "id = ?", arrayOf(userId.toString())
        ) > 0

    fun updateUserPhone(userId: Int, phone: String): Boolean =
        writableDatabase.update(
            TABLE_USERS,
            ContentValues().apply { put("phone", phone) },
            "id = ?", arrayOf(userId.toString())
        ) > 0

    fun loginUser(email: String, password: String): User? {
        val cursor = readableDatabase.query(
            TABLE_USERS, null,
            "email = ? AND password = ?",
            arrayOf(email.lowercase().trim(), hashPassword(password)),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) User(
                id      = it.getInt(it.getColumnIndexOrThrow("id")),
                name    = it.getString(it.getColumnIndexOrThrow("name")),
                email   = it.getString(it.getColumnIndexOrThrow("email")),
                phone   = it.getString(it.getColumnIndexOrThrow("phone")) ?: "",
                isAdmin = it.getInt(it.getColumnIndexOrThrow("is_admin")) == 1
            ) else null
        }
    }

    // ─────────────────────────────── PRODUCTS ─────────────────────────────── //

    fun getAllProducts(): List<Product> = queryProducts(null, null)

    fun getProductsByCategory(category: String): List<Product> =
        queryProducts("category = ?", arrayOf(category))

    private fun queryProducts(selection: String?, selectionArgs: Array<String>?): List<Product> {
        val list = mutableListOf<Product>()
        readableDatabase.query(
            TABLE_PRODUCTS, null, selection, selectionArgs, null, null, "name ASC"
        ).use { c ->
            while (c.moveToNext()) {
                list.add(
                    Product(
                        id          = c.getInt(c.getColumnIndexOrThrow("id")),
                        name        = c.getString(c.getColumnIndexOrThrow("name")),
                        price       = c.getDouble(c.getColumnIndexOrThrow("price")),
                        category    = c.getString(c.getColumnIndexOrThrow("category")),
                        description = c.getString(c.getColumnIndexOrThrow("description")) ?: "",
                        stock       = c.getInt(c.getColumnIndexOrThrow("stock")),
                        imageUri    = c.getString(c.getColumnIndexOrThrow("image_uri")) ?: ""
                    )
                )
            }
        }
        return list
    }

    fun insertProduct(product: Product): Long =
        writableDatabase.insert(TABLE_PRODUCTS, null, productValues(product))

    fun updateProduct(product: Product): Int =
        writableDatabase.update(
            TABLE_PRODUCTS, productValues(product), "id = ?", arrayOf(product.id.toString())
        )

    fun deleteProduct(productId: Int): Int =
        writableDatabase.delete(TABLE_PRODUCTS, "id = ?", arrayOf(productId.toString()))

    private fun productValues(p: Product) = ContentValues().apply {
        put("name",        p.name)
        put("price",       p.price)
        put("category",    p.category)
        put("description", p.description)
        put("stock",       p.stock)
        put("image_uri",   p.imageUri)
    }

    // ─────────────────────────────── ORDERS ───────────────────────────────── //

    /**
     * Saves an order in a single transaction:
     *   - 1:N  → one Order belongs to one User (user_id FK)
     *   - N:M  → one Order has many Products through order_items
     */
    fun saveOrder(
        userId: Int,
        cartItems: List<CartProduct>,
        total: Double,
        date: String
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val orderId = db.insert(TABLE_ORDERS, null, ContentValues().apply {
                put("user_id", userId)
                put("date",    date)
                put("status",  "En proceso")
                put("total",   total)
            })
            check(orderId != -1L) { "Failed to insert order" }

            cartItems.forEach { item ->
                db.insert(TABLE_ORDER_ITEMS, null, ContentValues().apply {
                    put("order_id",   orderId)
                    put("product_id", item.product.id)
                    put("quantity",   item.quantity)
                    put("unit_price", item.product.price)
                })
            }
            db.setTransactionSuccessful()
            orderId
        } finally {
            db.endTransaction()
        }
    }

    fun getOrdersForUser(userId: Int): List<Order> {
        val list = mutableListOf<Order>()
        readableDatabase.query(
            TABLE_ORDERS, null,
            "user_id = ?", arrayOf(userId.toString()),
            null, null, "date DESC"
        ).use { c ->
            while (c.moveToNext()) {
                val oid = c.getInt(c.getColumnIndexOrThrow("id"))
                list.add(
                    Order(
                        id          = oid.toString(),
                        date        = c.getString(c.getColumnIndexOrThrow("date")),
                        description = buildOrderDescription(oid),
                        total       = c.getDouble(c.getColumnIndexOrThrow("total")),
                        status      = c.getString(c.getColumnIndexOrThrow("status"))
                    )
                )
            }
        }
        return list
    }

    /** Joins order_items with products to build a human-readable description */
    private fun buildOrderDescription(orderId: Int): String {
        val items = mutableListOf<String>()
        readableDatabase.rawQuery(
            """
            SELECT p.name, oi.quantity
            FROM $TABLE_ORDER_ITEMS oi
            JOIN $TABLE_PRODUCTS p ON oi.product_id = p.id
            WHERE oi.order_id = ?
            """.trimIndent(),
            arrayOf(orderId.toString())
        ).use { c ->
            while (c.moveToNext()) {
                items.add("${c.getString(0)} x${c.getInt(1)}")
            }
        }
        return if (items.isEmpty()) "Sin artículos" else items.joinToString(", ")
    }
}
