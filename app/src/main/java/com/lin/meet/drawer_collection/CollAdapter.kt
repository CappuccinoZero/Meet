package com.lin.meet.drawer_collection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.know.KnowActivity
import com.lin.meet.R
import com.lin.meet.jsoup.LoveNewsBean
import com.lin.meet.personal.PersonalActivity
import com.lin.meet.recommend.RecommendActivity
import com.lin.meet.topic.TopicActivity
import com.lin.meet.video.VideoActivity

class CollAdapter:RecyclerView.Adapter<CollHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CollHolder {
        if(context == null)
            context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.collection_item,p0,false)
        return CollHolder(view)
    }

    override fun getItemCount(): Int {
        return colls.size
    }

    override fun onBindViewHolder(holder: CollHolder, i: Int) {
        holder.loadImage(colls[i].img)
        holder.loadHeader(colls[i].header)
        holder.nickName.text = colls[i].nickName
        holder.title.text = colls[i].title
        holder.time.text = colls[i].time
        holder.header.setOnClickListener {
            PersonalActivity.startOther(context as Activity,colls[i].uid)
        }
        var str = "推荐"
        when(colls[i].flag){
            1-> str = "话题"
            2-> str = "视频"
            3-> str = "提问"
        }
        holder.type.text = str
        holder.item.setOnClickListener {
            when(colls[i].flag){
                0->{val intent = Intent(context,RecommendActivity::class.java)
                    val bean = LoveNewsBean()
                    bean.contentUri = colls[i].id
                    bean.img = colls[i].img
                    bean.flag = colls[i].recommend
                    bean.author = colls[i].nickName
                    bean.time = colls[i].time
                    bean.setAbsoluteTitle(colls[i].title)
                    intent.putExtra("LoveNewsBean",bean)
                    context?.startActivity(intent) }
                1->{val intent = Intent(context, TopicActivity::class.java)
                    intent.putExtra("ID",colls[i].id)
                    intent.putExtra("isSender",false)
                    context?.startActivity(intent)}
                2->{val intent = Intent(context, VideoActivity::class.java)
                    intent.putExtra("VIDEO",colls[i].id)
                    intent.putExtra("UID",colls[i].uid)
                    context?.startActivity(intent)}
                3->{val intent = Intent(context, KnowActivity::class.java)
                    intent.putExtra("id",colls[i].id)
                    intent.putExtra("uid",colls[i].uid)
                    context?.startActivity(intent)}
            }
        }
    }

    fun insertContent(bean:CollBean){
        colls.add(bean)
        notifyDataSetChanged()
    }
    private val colls = ArrayList<CollBean>()
    private var context:Context ?= null
}