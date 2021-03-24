package com.pcbuilder.pcbuilder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_fragment.*

class ItemFragment : Fragment(R.layout.item_fragment) {
    private  val args: ItemFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)



    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemName.text = args.partname
        itemPrice.text = args.partprice.plus("\u20ac")
        var imgUrl = '"'.plus(args.partimg).plus('"')
        Glide.with(this).load(args.partimg).override(itemImg.width, itemImg.height).into(itemImg)

    }
}