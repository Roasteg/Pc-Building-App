package com.pcbuilder.pcbuilder

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import java.io.Console

class CartFragment : Fragment(R.layout.cart_fragment) {


    var dbGetter: GetDBCart? = null
    var compCursor: Cursor? = null
    var compAdapter: SimpleCursorAdapter? = null
    var db: SQLiteDatabase? = null

    private var cartView: ListView? = null
    private var totalAmount: TextView? = null
    var vi: View? = null
    var input: String? = null
    private var newPrice: String? = null
    private var stockPrice: String? = null
    private var totalPrice: String? = null
    private var cleanCartBtn: Button? = null
    private var saveCartBtn: Button? = null
    private var loadCartBtn: Button? = null
    private var listOfBuilds: MutableList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.cart_fragment, container, false)
        cartView = view.findViewById(R.id.cart_list)
        vi = inflater.inflate(R.layout.cart_items_layout, container, false)
        totalAmount = view.findViewById(R.id.totalAmount)
        cleanCartBtn = view.findViewById(R.id.clean_button)
        saveCartBtn = view.findViewById(R.id.save_button)
        loadCartBtn = view.findViewById(R.id.load_button)
        val wipeCart = AlertDialog.Builder(this.activity)

        wipeCart.setTitle("You're about to delete your WHOLE cart, continue?")
        wipeCart.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            clean()
            getCart()
            getTotalPrice()
            Snackbar.make(view, "Cart cleaned successfully", Snackbar.LENGTH_SHORT).show()
            dialog.cancel()
        }
        wipeCart.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.cancel()
        }



        var itemPos:Int = -1

        loadCartBtn!!.setOnClickListener {
            itemPos = -1
            getBuilds()
            val loadCart = AlertDialog.Builder(context)
            loadCart.setTitle("Choose your build")
            loadCart.setSingleChoiceItems(listOfBuilds.toTypedArray(), -1) { _, which ->
                itemPos = which

            }.create()
            loadCart.setPositiveButton("OK") { dialog, _ ->
                if (itemPos >= 0) {
                    load(listOfBuilds[itemPos])
                    getCart()
                    getTotalPrice()
                    dialog.cancel()
                } else {
                    dialog.cancel()
                }
            }
            loadCart.setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            loadCart.setNegativeButton("Delete") { dialog, _ ->
                if(itemPos == -1){
                    dialog.cancel()
                }
                else {
                    AlertDialog.Builder(context).setTitle("Are you sure?")
                        .setPositiveButton("OK") { dialog, _ ->

                            delete(listOfBuilds[itemPos])
                            Snackbar.make(
                                view,
                                "Build deleted successfully!",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            dialog.cancel()

                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }.show()
                }
            }

            loadCart.show()
        }
        val chars = arrayOf("Edit", "Delete")

        cartView!!.setOnItemClickListener { _, view, _, id ->
            val builder = AlertDialog.Builder(this.activity)
            val changeQuantity = AlertDialog.Builder(this.activity)
            val deleteItem = AlertDialog.Builder(this.activity)
            val text: TextView = view.findViewById(R.id.pQuantity)
            val priceNew: TextView = view.findViewById(R.id.pPrice)
            newPrice = priceNew.text.toString()
            input = text.text.toString()
            val edit = EditText(this.activity)
            changeQuantity.setTitle("Edit quantity")


            changeQuantity.setView(edit)

            edit.inputType = InputType.TYPE_CLASS_NUMBER
            edit.setText(input)

            changeQuantity.setPositiveButton(
                "OK"
            ) { dialog, _ ->

                input = edit.text.toString()
                if (input!!.toInt() > 0) {
                    getStockPrice(id.toString())
                    totalAmount!!.text = null
                    newPrice = ((stockPrice!!.toInt() * input!!.toInt()).toString())
                    updateCart(id.toString())
                    getCart()
                    getTotalPrice()
                    dialog.cancel()
                }

            }

            changeQuantity.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }

            deleteItem.setTitle("Are you sure?")
            deleteItem.setPositiveButton(
                "OK"
            ) { dialog, _ ->
                deleteFromCart(id.toString())
                getCart()
                getTotalPrice()
                dialog.cancel()

            }
            deleteItem.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }
            builder.setItems(chars) { _, which ->
                if (which == 0) {
                    changeQuantity.show()
                }
                if (which == 1) {
                    deleteItem.show()
                }

            }
            builder.show()
        }


        cleanCartBtn!!.setOnClickListener {
            if (cartView!!.count > 0) {
                wipeCart.show()
            } else {
                Snackbar.make(view, "There is nothing to delete", Snackbar.LENGTH_SHORT).show()
            }
        }
        saveCartBtn!!.setOnClickListener {
            if (cartView!!.count > 0) {
                val cartName = EditText(this.activity)
                val saveCart = AlertDialog.Builder(this.activity)
                saveCart.setTitle("Save cart...")
                saveCart.setView(cartName)
                saveCart.setPositiveButton(
                    "OK"
                ) { dialog, _ ->
                    getBuilds()
                    if (cartName.text.isNotEmpty() && !listOfBuilds.contains(cartName.text.toString())) {
                        save(cartName.text.toString())
                        dialog.cancel()

                    } else if (cartName.text.isNotEmpty() && listOfBuilds.contains(cartName.text.toString())) {
                        val exists = AlertDialog.Builder(this.activity)
                        exists.setTitle("Overwrite?")
                        exists.setPositiveButton("OK") { dialog, _ ->
                            overwrite(cartName.text.toString())
                            Snackbar.make(view, "Build overwritten", Snackbar.LENGTH_SHORT).show()
                            dialog.cancel()
                        }
                        exists.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()

                        }
                        exists.show()

                    } else {
                        Snackbar.make(view, "Enter build name!", Snackbar.LENGTH_SHORT).show()
                        dialog.cancel()
                    }

                }
                saveCart.setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.cancel()
                }


                saveCart.show()
            } else {
                Snackbar.make(view, "Cart is empty", Snackbar.LENGTH_SHORT).show()

            }
        }
        dbGetter = GetDBCart(activity?.applicationContext!!)
        dbGetter!!.database()
        return view
    }


    private fun updateCart(id: String) {
        db = dbGetter!!.open()
        db!!.execSQL("UPDATE new_cart SET item_Quantity = '$input', item_Price_New = '$newPrice' WHERE _id = $id")
        db!!.close()
    }

    private fun deleteFromCart(id: String) {
        db = dbGetter!!.open()
        db!!.execSQL("DELETE FROM new_cart WHERE _id = $id")
        db!!.close()
    }

    private fun getStockPrice(id: String) {
        db = dbGetter!!.open()
        val priceCursor =
            db!!.rawQuery("SELECT item_Stock_Price FROM new_cart WHERE _id = $id", null)
        priceCursor.moveToFirst()
        stockPrice = priceCursor.getString(priceCursor.getColumnIndex("item_Stock_Price"))
        db!!.close()
    }

    private fun getCart() {
        db = dbGetter!!.open()
        compCursor = db!!.rawQuery("SELECT * FROM " + GetDBCart.TABLE, null)
        val headers =
            arrayOf(GetDBCart.COLUMN_NAME, GetDBCart.COLUMN_PRICE_NEW, GetDBCart.COLUMN_QUANTITY)
        compAdapter = SimpleCursorAdapter(
            this.context,
            R.layout.cart_items_layout,
            compCursor,
            headers,
            intArrayOf(R.id.pName, R.id.pPrice, R.id.pQuantity),
            0
        )

        compAdapter!!.notifyDataSetChanged()
        cartView!!.deferNotifyDataSetChanged()
        cartView!!.adapter = compAdapter
        db!!.close()
    }

    private fun getTotalPrice() {
        db = dbGetter!!.open()
        val calculatePrice =
            db!!.rawQuery("SELECT SUM(item_Price_New) FROM " + GetDBCart.TABLE, null)
        calculatePrice.moveToFirst()
        totalPrice = calculatePrice.getString(calculatePrice.getColumnIndex("SUM(item_Price_New)"))
        if (totalPrice != null) {
            totalAmount!!.setText("Total amount = ".plus(totalPrice))
        } else {
            totalAmount!!.setText("Total amount = 0")
        }

        db!!.close()

    }

    private fun clean() {
        db = dbGetter!!.open()
        db!!.execSQL("DELETE FROM new_cart")
        db!!.close()
    }

    private fun save(cartName: String) {
        db = dbGetter!!.open()
        db!!.execSQL("CREATE TABLE $cartName AS SELECT * FROM new_cart")
        db!!.close()

    }

    private fun getBuilds() {
        listOfBuilds.clear()
        db = dbGetter!!.open()
        val cursor = db!!.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND (name NOT LIKE 'sqlite_sequence' AND name NOT LIKE 'android_metadata' AND name NOT LIKE 'new_cart')",
            null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            listOfBuilds.add(cursor.getString(cursor.getColumnIndex("name")))
            cursor.moveToNext()
        }
    }

    private fun load(cartName: String) {
        db = dbGetter!!.open()
        db!!.execSQL("DELETE FROM new_cart")
        db!!.execSQL("INSERT INTO new_cart SELECT * FROM $cartName")
        db!!.close()
    }

    private fun overwrite(cartName: String) {
        db = dbGetter!!.open()
        db!!.execSQL("DELETE FROM $cartName")
        db!!.execSQL("INSERT INTO $cartName SELECT * FROM new_cart")
        db!!.close()
    }

    private fun delete(cartName: String) {
        db = dbGetter!!.open()
        db!!.execSQL("DROP TABLE $cartName")

    }

    override fun onResume() {
        super.onResume()
        getCart()
        getTotalPrice()
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
        compCursor!!.close()
    }

}