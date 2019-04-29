package com.lin.meet.Know

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import de.hdodenhof.circleimageview.CircleImageView

class KnowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val header = itemView.findViewById<CircleImageView>(R.id.know_header)
    val nickName = itemView.findViewById<TextView>(R.id.nickName)
    val time = itemView.findViewById<TextView>(R.id.time)
    val content = itemView.findViewById<TextView>(R.id.content)
    val solve = itemView.findViewById<TextView>(R.id.knowed)
    fun loadHeader(context:Context,uri:String){
        Glide.with(context).load(uri).into(header)
    }
    fun setSolve(isSolve:Boolean){
        if(isSolve){
            solve.setText("问题已解决")
        }else{
            solve.setText("问题解决")
        }
    }

    fun setSolveVisible(visible:Boolean){
        solve.visibility = if(visible) View.VISIBLE else View.GONE
    }
}
class KnowCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val header = itemView.findViewById<CircleImageView>(R.id.comment_header)
    val nickName = itemView.findViewById<TextView>(R.id.comment_nickName)
    val time = itemView.findViewById<TextView>(R.id.comment_time)
    val content = itemView.findViewById<TextView>(R.id.content)
    val agree = itemView.findViewById<LinearLayout>(R.id.agree)
    val agree_img = itemView.findViewById<ImageView>(R.id.agree_img)
    val agree_text = itemView.findViewById<TextView>(R.id.agree_text)
    val agree_count = itemView.findViewById<TextView>(R.id.agree_count)
    fun loadHeader(context:Context,uri:String){
        Glide.with(context).load(uri).into(header)
    }
    fun setAgree(context: Context,agree:Boolean){
        if(agree){
            agree_img.setImageResource(R.drawable.agree)
            agree_text.setText("已赞同")
            agree_text.setTextColor(content.resources.getColor(R.color.colorAccent))
            agree_count.setTextColor(content.resources.getColor(R.color.colorAccent))
        }else{
            agree_img.setImageResource(R.drawable.disagree)
            agree_text.setTextColor(content.resources.getColor(R.color.text_back_1))
            agree_text.setText("赞同")
            agree_count.setTextColor(content.resources.getColor(R.color.text_back_1))
        }
    }
}