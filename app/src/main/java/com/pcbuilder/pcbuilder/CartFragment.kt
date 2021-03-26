package com.pcbuilder.pcbuilder

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment(R.layout.cart_fragment) {


    var dbGetter: GetDBCart? = null
    var compCursor: Cursor? = null
    var compAdapter: SimpleCursorAdapter? = null
    var db: SQLiteDatabase? = null

    var cartView: ListView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var totalAmount: TextView? = null
    var vi: View? = null
    var input: String? = null
    var new_price: String? = null
    var stock_price: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.cart_fragment, container, false)
        cartView = view.findViewById<ListView>(R.id.cart_list)
        vi = inflater.inflate(R.layout.cart_items_layout, container, false)
        totalAmount = view.findViewById(R.id.totalAmount)
        val chars = arrayOf("Edit", "Delete")
        cartView!!.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this.activity)
            val changeQuantity = AlertDialog.Builder(this.activity)
            val deleteItem = AlertDialog.Builder(this.activity)
            var text: TextView = view.findViewById(R.id.pQuantity)
            var price_New: TextView = view.findViewById(R.id.pPrice)
            new_price = price_New.text.toString()
            input = text.text.toString()
            changeQuantity.setTitle("Edit quantity")
            var edit = EditText(this.context)
            edit.inputType = InputType.TYPE_CLASS_NUMBER
            edit.setText(input)
            changeQuantity.setView(edit)
            changeQuantity.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    input = edit.text.toString()
                    if (input!!.toInt() > 0) {
                        getStockPrice(id.toString())
                        new_price = ((stock_price!!.toInt() * input!!.toInt()).toString())
                        updateCart(id.toString())
                        getCart()
                    }

                })

            changeQuantity.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })

            deleteItem.setTitle("Are you sure?")
            deleteItem.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->
                    deleteFromCart(id.toString())
                    getCart()
                })
            deleteItem.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            builder.setItems(chars, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    changeQuantity.show()
                }
                if(which == 1)
                {
                    deleteItem.show()
                }

            })
            builder.show()
        }
        dbGetter = GetDBCart(activity?.applicationContext!!)
        dbGetter!!.database()
        return view
    }


    fun updateCart(id:String) {
        db = dbGetter!!.open()
        db!!.execSQL("UPDATE new_cart SET item_Quantity = " + "'" + input + "'" + ", item_Price_New = " +"'"+ new_price + "'" +" WHERE _id = " + id)
    }

    fun deleteFromCart(id:String){
        db = dbGetter!!.open()
        db!!.execSQL("DELETE FROM new_cart WHERE _id = " + id)
    }

    fun getStockPrice(id: String)
    {
        db = dbGetter!!.open()
        var priceCursor = db!!.rawQuery("SELECT item_Stock_Price FROM new_cart WHERE _id = " + id, null)
        priceCursor.moveToFirst()
        stock_price = priceCursor.getString(priceCursor.getColumnIndex("item_Stock_Price"))
    }


    fun getCart() {
        db = dbGetter!!.open()
        compCursor = db!!.rawQuery("SELECT * FROM " + GetDBCart.TABLE, null)
        var headers =
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
    }

    override fun onResume() {
        super.onResume()
        getCart()
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
        compCursor!!.close()
    }

}