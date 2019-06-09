package com.lin.meet.main.fragment.Book

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R

class BaikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.baike_img)
    val view = itemView.findViewById<LinearLayout>(R.id.baike_view)
    val cnName = itemView.findViewById<TextView>(R.id.baike_cnName)
    val enName = itemView.findViewById<TextView>(R.id.baike_enName)
    val introduce  = itemView.findViewById<TextView>(R.id.baike_introduce)
    fun setImage(context: Context, uri:String){
        Glide.with(context).load(uri).into(image)
    }
}