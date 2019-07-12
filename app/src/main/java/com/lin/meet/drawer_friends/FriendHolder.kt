package com.lin.meet.drawer_friends

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import de.hdodenhof.circleimageview.CircleImageView

class FriendHolder(view: View): RecyclerView.ViewHolder(view) {
    private val header = view.findViewById<CircleImageView>(R.id.header)
    private val signature = view.findViewById<TextView>(R.id.signature)
    val nickName = view.findViewById<TextView>(R.id.nickName)
    val item = view.findViewById<View>(R.id.friendItem)

    fun loadHeader(uri:String){Glide.with(header.context).load(uri).into(header)}
    @SuppressLint("SetTextI18n")
    fun loadSignature(str:String){
        if(str.isEmpty())
            return
        if(str.length<=22)
            signature.text = str
        else
            signature.text = str.substring(0,22)+"..."
    }
}