package com.example.plutoaccessories.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // ================= USERS =================
        db.execSQL(
            "CREATE TABLE " + TABLE_USERS + " (" +
                    "id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nama TEXT, " +
                    "email TEXT UNIQUE, " +
                    "password TEXT, " +
                    "alamat TEXT, " +
                    "no_hp TEXT, " +
                    "role TEXT DEFAULT 'user');"
        )

        // ================= PRODUCTS =================
        db.execSQL(
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    "id_produk INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nama_produk TEXT, " +
                    "deskripsi TEXT, " +
                    "harga REAL, " +
                    "stok INTEGER, " +
                    "gambar TEXT, " +
                    "merk TEXT, " +
                    "bahan TEXT);"
        )

        // ================= ORDERS =================
        db.execSQL(
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    "id_order INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_user INTEGER, " +
                    "tanggal TEXT, " +
                    "total_harga REAL, " +
                    "status TEXT, " +
                    "FOREIGN KEY (id_user) REFERENCES users(id_user));"
        )

        // ================= ORDER ITEMS =================
        db.execSQL(
            "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                    "id_item INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_order INTEGER, " +
                    "id_produk INTEGER, " +
                    "jumlah INTEGER, " +
                    "subtotal REAL, " +
                    "FOREIGN KEY (id_order) REFERENCES orders(id_order), " +
                    "FOREIGN KEY (id_produk) REFERENCES products(id_produk));"
        )

        // ================= WALLET =================
        db.execSQL(
            "CREATE TABLE " + TABLE_WALLET + " (" +
                    "id_wallet INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_user INTEGER, " +
                    "saldo REAL DEFAULT 0, " +
                    "FOREIGN KEY (id_user) REFERENCES users(id_user));"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET)
        onCreate(db)
    }

    // ================= CRUD USERS =================
    fun registerUser(
        nama: String?,
        email: String?,
        password: String?,
        alamat: String?,
        no_hp: String?
    ): Boolean {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put("nama", nama)
        values.put("email", email)
        values.put("password", password)
        values.put("alamat", alamat)
        values.put("no_hp", no_hp)

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun loginUser(email: String?, password: String?): Cursor {
        val db = this.getReadableDatabase()
        return db.rawQuery(
            "SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
            arrayOf<String?>(email, password)
        )
    }

    // ================= CRUD PRODUCTS =================
    fun addProduct(
        nama: String?,
        deskripsi: String?,
        harga: Double,
        stok: Int,
        gambar: String?,
        merk: String?,
        bahan: String?
    ): Boolean {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put("nama_produk", nama)
        values.put("deskripsi", deskripsi)
        values.put("harga", harga)
        values.put("stok", stok)
        values.put("gambar", gambar)
        values.put("merk", merk)
        values.put("bahan", bahan)

        val result = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return result != -1L
    }

    val allProducts: Cursor
        get() {
            val db = this.getReadableDatabase()
            return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null)
        }

    // ================= WALLET =================
    fun createWallet(id_user: Int) {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put("id_user", id_user)
        values.put("saldo", 0)
        db.insert(TABLE_WALLET, null, values)
        db.close()
    }

    fun getSaldo(id_user: Int): Double {
        val db = this.getReadableDatabase()
        val cursor = db.rawQuery(
            "SELECT saldo FROM " + TABLE_WALLET + " WHERE id_user=?",
            arrayOf<String>(id_user.toString())
        )
        if (cursor.moveToFirst()) {
            val saldo = cursor.getDouble(0)
            cursor.close()
            return saldo
        }
        return 0.0
    }

    fun updateSaldo(id_user: Int, saldoBaru: Double) {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put("saldo", saldoBaru)
        db.update(TABLE_WALLET, values, "id_user=?", arrayOf<String>(id_user.toString()))
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "db_pluto_9.db"
        private const val DATABASE_VERSION = 1

        // Nama tabel
        const val TABLE_USERS: String = "users"
        const val TABLE_PRODUCTS: String = "products"
        const val TABLE_ORDERS: String = "orders"
        const val TABLE_ORDER_ITEMS: String = "order_items"
        const val TABLE_WALLET: String = "wallet"
    }
}