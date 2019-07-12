package com.lin.meet.drawer_dynamic

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import com.lin.meet.bean.DefaultUtil
import de.hdodenhof.circleimageview.CircleImageView

class DynamicHolder(view: View):RecyclerView.ViewHolder(view) {
    val header = view.findViewById<CircleImageView>(R.id.header)
    val nickName = view.findViewById<TextView>(R.id.nickName)
    val title = view.findViewById<TextView>(R.id.title)
    val img = view.findViewById<ImageView>(R.id.img)
    val item = view.findViewById<View>(R.id.dynamicCard)
    val type = view.findViewById<TextView>(R.id.type)

    fun loadHeader(uri:String){
        if(uri.isEmpty()||uri == "@null")
            return
        Glide.with(header.context).load(uri).into(header)
    }

    fun loadImage(uri:String){
        if(uri.isNotEmpty()&&uri != "@null"){
            Glide.with(img.context).load(uri).into(img)
        }else{
            Glide.with(img.context).load(DefaultUtil.getRandomAnimalPicture()).into(img)
        }
    }
}