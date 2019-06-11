package com.lin.meet.main.fragment.Book

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R

class BaikeViewHolder(itemView: View,top:Boolean) : RecyclerView.ViewHolder(itemView) {
    val top = top
    var image:ImageView ?= null
    var view:LinearLayout ?= null
    var cnName:TextView ?= null
    var enName:TextView ?= null
    var introduce:TextView ?= null
    fun setImage(context: Context, uri:String){
        Glide.with(context).load(uri).into(image!!)
    }
    init {
        if(!top){
            image = itemView.findViewById<ImageView>(R.id.baike_img)
            view = itemView.findViewById<LinearLayout>(R.id.baike_view)
            cnName = itemView.findViewById<TextView>(R.id.baike_cnName)
            enName = itemView.findViewById<TextView>(R.id.baike_enName)
            introduce  = itemView.findViewById<TextView>(R.id.baike_introduce)
        }
    }
    constructor(itemView: View):this(itemView,false)
}