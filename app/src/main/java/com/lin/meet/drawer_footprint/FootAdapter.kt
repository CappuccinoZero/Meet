package com.lin.meet.drawer_footprint

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.bean.MapFlag

class FootAdapter(callback:FootConstract.ItemCallback):RecyclerView.Adapter<FootHolder>() {
    val callback = callback
    val foots = ArrayList<MapFlag>()
    var context: Context?= null
    var select = -1
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FootHolder {
        if(context==null)
            context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.foot_item,p0,false)
        return FootHolder(view)
    }

    override fun getItemCount(): Int {
        return foots.size
    }

    override fun onBindViewHolder(holder: FootHolder, i: Int) {
        holder.content.text = foots[i].content
        holder.time.text = foots[i].createdAt
        holder.loadImage(foots[i].image)
        holder.item.setOnClickListener { callback.selectItem(i) }
        if(select == i){
            select = -1
            val set = AnimatorSet()
            val animatorX = ObjectAnimator.ofFloat(holder.item,"ScaleX",1f,1.1f,1f)
            val animatorY = ObjectAnimator.ofFloat(holder.item,"ScaleY",1f,1.1f,1f)
            set.play(animatorX).with(animatorY)
            set.duration = 400
            set.start()
        }
    }

    fun insertMapFlag(flag:MapFlag):Int{
        foots.add(flag)
        notifyDataSetChanged()
        return foots.size-1
    }

    fun getMapFlag(position:Int):MapFlag{
        return foots[position]
    }

    fun setItemSelect(i:Int){
        select = i
        notifyDataSetChanged()
    }

}