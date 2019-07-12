package com.lin.meet.drawer_message.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.comment.CommentActivity
import com.lin.meet.drawer_message.Bean.Reply
import com.lin.meet.drawer_message.view_holder.ReplyHolder
import com.lin.meet.know.KnowActivity
import com.lin.meet.topic.TopicActivity
import com.lin.meet.video.VideoActivity

class ReplyAdapter: RecyclerView.Adapter<ReplyHolder>() {
    private val replys = ArrayList<Reply>()
    private var context:Context ?= null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReplyHolder {
        if(context == null)
            context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.message_item, viewGroup, false)
        return ReplyHolder(view)
    }

    override fun getItemCount(): Int {
        return replys.size
    }

    override fun onBindViewHolder(holder: ReplyHolder, i: Int) {
        holder.nickname.text = replys[i].getNickname()
        holder.loadHeader(replys[i].getHeaderUri())
        holder.time.text = replys[i].getTime()
        holder.replyContent.text = replys[i].getReplyContent()
        holder.loadImg(replys[i].getImg())
        holder.content.text = replys[i].getContent()
        holder.item.setOnClickListener {
            if(replys[i].isMain()){
                when(replys[i].getFlag()){
                    1->{
                        val intent = Intent(context,TopicActivity::class.java)
                        intent.putExtra("ID",replys[i].getId())
                        intent.putExtra("isSender",false)
                        context?.startActivity(intent)
                    }
                    2->{
                        val intent = Intent(context,VideoActivity::class.java)
                        intent.putExtra("VIDEO",replys[i].getId())
                        intent.putExtra("UID",replys[i].getParentUid())
                        context?.startActivity(intent)
                    }
                    3->{
                        val intent = Intent(context,KnowActivity::class.java)
                        intent.putExtra("id",replys[i].getId())
                        intent.putExtra("uid",replys[i].getParentUid())
                        context?.startActivity(intent)
                    }
                }
            }else{
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("id", replys[i].getId())
                intent.putExtra("flag", replys[i].getFlag())
                context?.startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
            }
        }
    }

    fun refreshReply(){
        replys.clear()
        notifyDataSetChanged()
    }

    fun insertReply(reply:Reply){
        for (index in 0 until replys.size){
            if(reply.getSize()>replys[index].getSize()){
                replys.add(index,reply)
                notifyDataSetChanged()
                return
            }
        }
        replys.add(reply)
        notifyDataSetChanged()
    }
}