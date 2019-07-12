package com.lin.meet.drawer_dynamic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.db_bean.picture_main
import com.lin.meet.know.KnowActivity
import com.lin.meet.personal.PersonalActivity
import com.lin.meet.picture_observer.PictureObserver
import com.lin.meet.topic.TopicActivity
import com.lin.meet.video.VideoActivity

class DynamicAdapter:RecyclerView.Adapter<DynamicHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DynamicHolder {
        if(context == null)
            context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.dynamic_item,p0,false)
        return DynamicHolder(view)
    }

    override fun getItemCount(): Int {
        return dynamics.size
    }

    override fun onBindViewHolder(holder: DynamicHolder, i: Int) {
        holder.loadImage(dynamics[i].img)
        holder.loadHeader(dynamics[i].header)
        holder.nickName.text = dynamics[i].nickName
        holder.title.text = dynamics[i].title
        holder.header.setOnClickListener { PersonalActivity.startOther(context as Activity,dynamics[i].uid) }
        holder.nickName.setOnClickListener { PersonalActivity.startOther(context as Activity,dynamics[i].uid) }
        val str:String = when(dynamics[i].flag){
            1-> "话题"
            2-> "视频"
            3-> "提问"
            else -> "图片"
        }
        holder.type.text = str
        holder.item.setOnClickListener {
            when(dynamics[i].flag){
                1->{val intent = Intent(context, TopicActivity::class.java)
                    intent.putExtra("ID",dynamics[i].id)
                    intent.putExtra("isSender",false)
                    context?.startActivity(intent)}
                2->{val intent = Intent(context, VideoActivity::class.java)
                    intent.putExtra("VIDEO",dynamics[i].id)
                    intent.putExtra("UID",dynamics[i].uid)
                    context?.startActivity(intent)}
                3->{val intent = Intent(context, KnowActivity::class.java)
                    intent.putExtra("id",dynamics[i].id)
                    intent.putExtra("uid",dynamics[i].uid)
                    context?.startActivity(intent)}
                else->{
                    val bean = picture_main()
                    bean.content = dynamics[i].pContent
                    bean.tltle = dynamics[i].title
                    bean.id = dynamics[i].id
                    bean.uid = dynamics[i].uid
                    bean.uri = dynamics[i].img
                    val intent = Intent(context, PictureObserver::class.java)
                    intent.putExtra("bean",bean)
                    intent.putExtra("haveContent",true)
                    context?.startActivity(intent)
                }
            }
        }
    }

    fun insertContent(bean: DynamicBean){
        dynamics.add(bean)
        notifyDataSetChanged()
    }

    fun clearData(){
        dynamics.clear()
        notifyDataSetChanged()
    }
    private val dynamics = ArrayList<DynamicBean>()
    private var context:Context ?= null
}