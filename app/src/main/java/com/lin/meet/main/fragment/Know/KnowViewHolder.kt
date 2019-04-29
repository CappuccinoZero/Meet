package com.lin.meet.main.fragment.Know

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import de.hdodenhof.circleimageview.CircleImageView

class KnowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val item = itemView.findViewById<CardView>(R.id.know_item)
    val imgLayout = itemView.findViewById<CardView>(R.id.img_layout)
    val img = itemView.findViewById<ImageView>(R.id.img)
    val header = itemView.findViewById<CircleImageView>(R.id.know_header)
    val nickName = itemView.findViewById<TextView>(R.id.know_nickName)
    val content = itemView.findViewById<TextView>(R.id.know_content)
    val problem = itemView.findViewById<TextView>(R.id.know_text2)

    fun setProblem(OK: Boolean){
        problem.text = if(OK) "已解决" else "未解决"
    }

    fun setHeader(context:Context,uri:String){
        Glide.with(context).load(uri).into(header)
    }

    fun setImg(context:Context,uri:String){
        if(uri == "@null"){
            imgLayout.visibility = View.GONE
        }else{
            imgLayout.visibility = View.VISIBLE
            Glide.with(context).load(uri).into(img)
        }
    }

    fun visibleImg(visible:Boolean){
        img.visibility = if(visible) View.VISIBLE else View.GONE
    }

    fun setContent(content:String){
        if(content == null)
            this.content.text = "提问："
        else
            this.content.text = "提问："+content
    }
}