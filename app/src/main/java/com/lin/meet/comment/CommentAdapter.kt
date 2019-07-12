package com.lin.meet.comment

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lin.meet.R
import com.lin.meet.db_bean.Reply
import com.lin.meet.personal.PersonalActivity
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter() :RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){
    var context: Context?= null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentViewHolder {
        if(context == null)
            context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.comment_son_item, viewGroup, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, i: Int) {
        holder.nickName.text = comments[i].nickName
        holder.content.text = comments[i].bean.content
        holder.time.text = comments[i].bean.createdAt
        holder.loadHeader(comments[i].headUri)
        holder.header.setOnClickListener {
            PersonalActivity.startOther(context as Activity,comments[i].bean.uid)
        }
    }

    val comments = ArrayList<Reply>()
    class CommentViewHolder(view: View):RecyclerView.ViewHolder(view){
        val nickName = view.findViewById<TextView>(R.id.nickName)
        val header = view.findViewById<CircleImageView>(R.id.header)
        val content = view.findViewById<TextView>(R.id.content)
        val time = view.findViewById<TextView>(R.id.time)
        fun loadHeader(uri:String){
            Glide.with(header.context).load(uri).into(header)
        }
    }

    fun insertComment(comment:Reply){
        comments.add(comment)
        notifyDataSetChanged()
    }

    fun insertComment(i:Int,comment:Reply) {
        comments.add(i,comment)
        notifyDataSetChanged()
    }

    fun refreshComment(){
        comments.clear()
        notifyDataSetChanged()
    }
}