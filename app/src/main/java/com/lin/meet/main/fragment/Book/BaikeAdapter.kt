package com.lin.meet.main.fragment.Book

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.transition.Explode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.bean.Baike
import com.lin.meet.encyclopedia.EncyclopediaActivity

class BaikeAdapter(activity: Activity): RecyclerView.Adapter<BaikeViewHolder>() {
    private var baikes:ArrayList<Baike> ?= null
    private var context: Context ?= null
    private val activity = activity
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaikeViewHolder {
        if(context==null)
            context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.baike_view,p0,false)
        return BaikeViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(baikes!=null)
            return baikes!!.size
        return 0
    }

    override fun onBindViewHolder(viewHolder: BaikeViewHolder, i: Int) {
        viewHolder.cnName.text = baikes!![i].cnName
        viewHolder.enName.text = baikes!![i].enName
        viewHolder.introduce.text = baikes!![i].brief
        viewHolder.setImage(context!!,baikes!![i].imageUri)
        viewHolder.view.setOnClickListener {
            val intent = Intent (context,EncyclopediaActivity::class.java)
            intent.putExtra("Baike",true)
            intent.putExtra("cnName",baikes!![i].cnName)
            intent.putExtra("enName",baikes!![i].enName)
            intent.putExtra("imageUri",baikes!![i].imageUri)
            intent.putExtra("url",baikes!![i].uri)
            intent.putExtra("type",baikes!![i].type)
            activity.window.exitTransition = Explode()
            val pair1:Pair<View,String> = Pair(viewHolder.image,ViewCompat.getTransitionName(viewHolder.image))
            val pair2:Pair<View,String> = Pair(viewHolder.cnName,ViewCompat.getTransitionName(viewHolder.cnName))
            val pair3:Pair<View,String> = Pair(viewHolder.enName,ViewCompat.getTransitionName(viewHolder.enName))
            val pair4:Pair<View,String> = Pair(viewHolder.cnName,"cnTitle")
            val compact = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2,pair3,pair4)
            ActivityCompat.startActivity(activity,intent,compact.toBundle())
        }
    }

    fun refreshAdapter(){
        if(baikes != null){
            baikes!!.clear()
            baikes = null
        }
        baikes = ArrayList()
        notifyDataSetChanged()
    }

    fun insertBaike(baike:Baike):Int{
        if(baikes==null)
            return -1
        baikes!!.add(baike)
        notifyDataSetChanged()
        return baikes!!.size - 1
    }

    fun insertBaikeToTop(position:Int,baike:Baike):Int{
        if(baikes==null)
            return -1
        baikes!!.add(position,baike)
        notifyDataSetChanged()
        return baikes!!.size - 1
    }

}