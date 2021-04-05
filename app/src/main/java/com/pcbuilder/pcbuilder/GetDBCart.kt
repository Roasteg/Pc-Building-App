package com.pcbuilder.pcbuilder

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*

class GetDBCart internal constructor(private val myContext: Context) :
    SQLiteOpenHelper(myContext, DB_NAME, null, SCHEMA) {
    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    companion object {
        private var DB_PATH: String? = null
        private const val DB_NAME = "cart.db"
        private const val SCHEMA = 9
        var TABLE: String = "new_cart"
        var COLUMN_NAME: String = "item_Name"
        var COLUMN_PRICE_NEW: String = "item_Price_New"
        var COLUMN_PRICE_STOCK: String = "item_Price_New"
        var COLUMN_QUANTITY: String = "item_Quantity"
        var COLUMN_CATEGORY: String = "item_Category"
        const val COLUMN_ID = "_id"
    }

    fun database() {
        var myInput: InputStream? = null
        var myOutput: OutputStream? = null
        try {
            val file = File(DB_PATH)
            if (!file.exists()) {
                myInput = myContext.assets.open(DB_NAME)
                val outFileName = DB_PATH
                myOutput = FileOutputStream(outFileName)
                val buffer = ByteArray(1024)
                var length: Int
                while (myInput.read(buffer).also { length = it } > 0) {
                    myOutput.write(buffer, 0, length)
                }
                myOutput.flush()
            }
        } catch (ex: IOException) {
            Log.d("GetDB", ex.message!!)
        } finally {
            try {
                myOutput?.close()
                myInput?.close()
            } catch (ex: IOException) {
                Log.d("GetDB", ex.message!!)
            }
        }
    }

    @Throws(SQLException::class)
    fun open(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(DB_PATH!!, null, SQLiteDatabase.OPEN_READWRITE)
    }


    init {
        DB_PATH = myContext.filesDir.path + DB_NAME
    }
}