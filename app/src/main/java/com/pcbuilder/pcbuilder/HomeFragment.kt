package com.pcbuilder.pcbuilder

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment(R.layout.home_fragment),
    NavigationView.OnNavigationItemSelectedListener {
    var compList: ListView? = null
    var main: MainActivity? = null
    //db
    var dbGetter: GetDB? = null
    var compCursor: Cursor? = null
    var compAdapter: SimpleCursorAdapter? = null
    var imgCursor: Cursor? = null
    var infoCursor: Cursor? = null
    var imgAdapter: SimpleCursorAdapter? = null
    var db: SQLiteDatabase? = null
    var homeFrag: HomeFragment? = null
    var img: ImageView? = null


    var imgInfo:String? = null

    private var drawer: DrawerLayout? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawer = activity?.findViewById(R.id.drawer_layout)
        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            activity, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        var navigaion: NavigationView = requireActivity().findViewById(R.id.navigation_view)

        navigaion.setNavigationItemSelectedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.home_fragment, container, false)
        val v: View = inflater.inflate(R.layout.list_adapter_view, container, false)
        compList = view.findViewById<ListView>(R.id.componentList)
        img = v.findViewById(R.id.cImg)
        dbGetter = GetDB(activity?.applicationContext!!)
        dbGetter!!.database()

        compList!!.setOnItemClickListener { parent, view, position, id ->
            var name = view.findViewById<TextView>(R.id.cName).text.toString()
            var price = view.findViewById<TextView>(R.id.cPrice).text.toString()
            additInfo(name)
            val action = HomeFragmentDirections.actionHomeFragmentToItemFragment(name, price,
                imgInfo.toString())
            findNavController().navigate(action)
        }
        return view
    }





    fun refreshDb() {
        db = dbGetter!!.open()
        compCursor = db!!.rawQuery("SELECT * FROM " + GetDB.TABLE, null)
        var headers = arrayOf(GetDB.COLUMN_NAME, GetDB.COLUMN_PRICE)
        compAdapter = SimpleCursorAdapter(
            this.context,
            R.layout.list_adapter_view,
            compCursor,
            headers,
            intArrayOf(R.id.cName, R.id.cPrice),
            0
        )




        compAdapter!!.notifyDataSetChanged()
        compList!!.deferNotifyDataSetChanged()
        compList!!.adapter = compAdapter


    }

    fun additInfo(name:String)
    {
        db = dbGetter!!.open()
        infoCursor = db!!.rawQuery("SELECT * FROM " + GetDB.TABLE + " WHERE " + GetDB.COLUMN_NAME + " = " + '"' + name + '"', null)
        infoCursor!!.moveToFirst()
        imgInfo = infoCursor!!.getString(infoCursor!!.getColumnIndex("img"))
    }

    override fun onResume() {
        super.onResume()
        refreshDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
        compCursor!!.close()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        GetDB.TABLE = item.titleCondensed.toString()
        GetDB.COLUMN_NAME = GetDB.TABLE.plus("_Name")
        GetDB.COLUMN_PRICE = GetDB.TABLE.plus("_Price")
        refreshDb()
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }


}