package com.lin.meet.drawer_footprint

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R

class FootHolder(view: View):RecyclerView.ViewHolder(view) {
    val time = view.findViewById<TextView>(R.id.time)
    val content = view.findViewById<TextView>(R.id.content)
    val img = view.findViewById<ImageView>(R.id.img)
    val item = view.findViewById<View>(R.id.item)
    fun loadImage(uri:String){
        Glide.with(img.context).load(uri).into(img)
    }
}