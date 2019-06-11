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
import com.lin.meet.recommend.RecommendConstract

class BaikeAdapter(activity: Activity): RecyclerView.Adapter<BaikeViewHolder>() {
    private var baikes:ArrayList<Baike> ?= null
    private var context: Context ?= null
    private val activity = activity
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaikeViewHolder {
        if(context==null)
            context = p0.context
        if(p1 == 0){
            val view = LayoutInflater.from(context).inflate(R.layout.top_view,p0,false)
            return BaikeViewHolder(view,true)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.baike_view,p0,false)
            return BaikeViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if(baikes!=null)
            return baikes!!.size+1
        return 1
    }

    override fun onBindViewHolder(viewHolder: BaikeViewHolder, i: Int) {
        if(!viewHolder.top){
            viewHolder.cnName?.text = baikes!![i-1].cnName
            viewHolder.enName?.text = baikes!![i-1].enName
            viewHolder.introduce?.text = baikes!![i-1].brief
            viewHolder.setImage(context!!,baikes!![i-1].imageUri)
            viewHolder.view?.setOnClickListener {
                if(hideCallback!=null)hideCallback!!.setVisiable(false)
                val intent = Intent (context, EncyclopediaActivity::class.java)
                intent.putExtra("Baike",true)
                intent.putExtra("cnName",baikes!![i-1].cnName)
                intent.putExtra("enName",baikes!![i-1].enName)
                intent.putExtra("imageUri",baikes!![i-1].imageUri)
                intent.putExtra("url",baikes!![i-1].uri)
                intent.putExtra("type",baikes!![i-1].type)
                activity.window.exitTransition = Explode()
                val pair1: Pair<View, String> = Pair(viewHolder.image, viewHolder.image?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair2: Pair<View, String> = Pair(viewHolder.cnName, viewHolder.cnName?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair3: Pair<View, String> = Pair(viewHolder.enName, viewHolder.enName?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair4: Pair<View, String> = Pair(viewHolder.cnName,"cnTitle")
                val compact = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2,pair3,pair4)
                ActivityCompat.startActivity(activity,intent,compact.toBundle())
            }
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

    override fun getItemViewType(position: Int): Int {
        if(position == 0)
            return 0
        return 1
    }

    internal var hideCallback: RecommendConstract.searchCallback? = null
    fun setHideCallback(hideCallback: RecommendConstract.searchCallback) {
        this.hideCallback = hideCallback
    }
}