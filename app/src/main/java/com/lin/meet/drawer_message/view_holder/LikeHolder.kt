package com.lin.meet.drawer_message.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import de.hdodenhof.circleimageview.CircleImageView

class LikeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nickname = itemView.findViewById<TextView>(R.id.nickName)
    val content = itemView.findViewById<TextView>(R.id.content)
    val likeText = itemView.findViewById<TextView>(R.id.likeText)
    val time = itemView.findViewById<TextView>(R.id.time)
    val header = itemView.findViewById<CircleImageView>(R.id.header)
    val item:View = itemView.findViewById(R.id.replyItem)
    fun loadHeader(uri:String){
        Glide.with(header.context).load(uri).into(header)}
}