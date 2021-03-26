package com.pcbuilder.pcbuilder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_fragment.*

class ItemFragment : Fragment(R.layout.item_fragment) {

    var dbGetter: GetDBCart? = null
    var compCursor: Cursor? = null
    var compAdapter: SimpleCursorAdapter? = null
    var db: SQLiteDatabase? = null

    var addToCart: Button? = null

    private val args: ItemFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.item_fragment, container, false)

        dbGetter = GetDBCart(activity?.applicationContext!!)
        dbGetter!!.database()
        addToCart = view.findViewById(R.id.addToCart)

        addToCart!!.setOnClickListener {
            addToCart(
                args.partname, args.partprice,
                args.partsocket.toString(), view
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemName.text = args.partname
        itemPrice.text = args.partprice.plus("\u20ac")
        itemSocket.text = "Socket: ".plus(args.partsocket)
        Glide.with(this).load(args.partimg).override(itemImg.width, itemImg.height).into(itemImg)

    }


    fun addToCart(name: String, price: String, socket: String, view: View) {
        db = dbGetter!!.open()
        var check: Cursor = db!!.rawQuery(
            "SELECT * FROM " + GetDBCart.TABLE + " WHERE item_Category = " + "'".plus(GetDB.TABLE)
                .plus("'"), null
        )
        if (check != null && check.count>0) {
            Snackbar.make(view, R.string.itemInCart, Snackbar.LENGTH_SHORT).show()
            check.close()
        } else {
            db!!.execSQL(
                "INSERT INTO new_cart(item_Price_New, item_Name, item_Quantity, item_Socket, item_Stock_Price, item_Category) " +
                        "VALUES(" + "'".plus(price).plus("',") + "'".plus(name)
                    .plus("',") + "1," + "'".plus(socket).plus("',") + "'".plus(price)
                    .plus("',") + "'".plus(GetDB.TABLE).plus("'") + ")"
            )
            Snackbar.make(view, R.string.addedToCart, Snackbar.LENGTH_SHORT).show()

        }
    }
}