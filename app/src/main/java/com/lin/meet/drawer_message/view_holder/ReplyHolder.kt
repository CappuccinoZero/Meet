package com.lin.meet.drawer_message.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import de.hdodenhof.circleimageview.CircleImageView

class ReplyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nickname = itemView.findViewById<TextView>(R.id.nickName)
    val content = itemView.findViewById<TextView>(R.id.content)
    val replyContent = itemView.findViewById<TextView>(R.id.replyContent)
    val time = itemView.findViewById<TextView>(R.id.time)
    val header = itemView.findViewById<CircleImageView>(R.id.header)
    val img = itemView.findViewById<ImageView>(R.id.img)
    val item:View = itemView.findViewById(R.id.replyItem)
    fun setImgVisiable(show:Boolean){img.visibility = if (show) View.VISIBLE else View.GONE}
    fun loadHeader(uri:String){
        Glide.with(header.context).load(uri).into(header)}
    fun loadImg(uri:String){
        if(uri!="@null"&&uri.isNotEmpty()){
            setImgVisiable(true)
            Glide.with(img.context).load(uri).into(img)
        }else{
            setImgVisiable(false)
        }
    }
}