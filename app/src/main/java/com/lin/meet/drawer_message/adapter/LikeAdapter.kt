package com.lin.meet.drawer_message.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.comment.CommentActivity
import com.lin.meet.drawer_message.Bean.Like
import com.lin.meet.drawer_message.view_holder.LikeHolder
import com.lin.meet.know.KnowActivity
import com.lin.meet.topic.TopicActivity
import com.lin.meet.video.VideoActivity

class LikeAdapter: RecyclerView.Adapter<LikeHolder>() {
    private var context:Context ?= null
    private val likes = ArrayList<Like>()
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LikeHolder {
        if(context == null)
            context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.message_like_item, viewGroup, false)
        return LikeHolder(view)
    }

    override fun getItemCount(): Int {
        return likes.size
    }

    override fun onBindViewHolder(holder: LikeHolder, i: Int) {
        holder.nickname.text = likes[i].getNickname()
        holder.content.text = likes[i].getContent()
        holder.likeText.text = likes[i].getReplyContent()
        holder.loadHeader(likes[i].getHeaderUri())
        holder.time.text = likes[i].getTime()
        holder.item.setOnClickListener {
            if(likes[i].isMain()){
                when(likes[i].getFlag()){
                    1->{val intent = Intent(context, TopicActivity::class.java)
                        intent.putExtra("ID",likes[i].getId())
                        intent.putExtra("isSender",false)
                        context?.startActivity(intent)}
                    2->{val intent = Intent(context, VideoActivity::class.java)
                        intent.putExtra("VIDEO",likes[i].getId())
                        intent.putExtra("UID",likes[i].getParentUid())
                        context?.startActivity(intent)}
                }
            }
            else if(likes[i].getFlag()<3){
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("id", likes[i].getId())
                intent.putExtra("flag", likes[i].getFlag())
                context?.startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
            }else{
                val intent = Intent(context, KnowActivity::class.java)
                intent.putExtra("id",likes[i].getId())
                intent.putExtra("uid",likes[i].getParentUid())
                context?.startActivity(intent)
            }
        }
    }

    fun refreshReply(){
        likes.clear()
        notifyDataSetChanged()
    }

    fun insertReply(like: Like){
        for (index in 0 until likes.size){
            if(like.getSize()>likes[index].getSize()){
                likes.add(index,like)
                notifyDataSetChanged()
                return
            }
        }
        likes.add(like)
        notifyDataSetChanged()
    }
}