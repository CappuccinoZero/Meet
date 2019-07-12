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
import com.lin.meet.main.fragment.Know.LoadingViewHolder
import com.lin.meet.recommend.RecommendConstract

class BaikeAdapter(activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var baikes:ArrayList<Baike> ?= null
    private var context: Context ?= null
    private val activity = activity
    private var isLoading = false
    private var error = false
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if(context==null)
            context = p0.context
        if(p1==1){
            val view = LayoutInflater.from(context).inflate(R.layout.baike_view,p0,false)
            return BaikeViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.loading_item_2,p0,false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if(baikes!=null)
            return baikes!!.size+1
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        if(position==baikes?.size)
            return 0
        return 1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if(viewHolder is BaikeViewHolder){
            viewHolder.cnName?.text = baikes!![i].cnName
            viewHolder.enName?.text = baikes!![i].enName
            viewHolder.introduce?.text = baikes!![i].brief
            viewHolder.setImage(context!!,baikes!![i].imageUri)
            viewHolder.view?.setOnClickListener {
                if(hideCallback!=null)hideCallback!!.setVisiable(false)
                val intent = Intent (context, EncyclopediaActivity::class.java)
                intent.putExtra("Baike",true)
                intent.putExtra("cnName",baikes!![i].cnName)
                intent.putExtra("enName",baikes!![i].enName)
                intent.putExtra("imageUri",baikes!![i].imageUri)
                intent.putExtra("url",baikes!![i].uri)
                intent.putExtra("type",baikes!![i].type)
                activity.window.exitTransition = Explode()
                val pair1: Pair<View, String> = Pair(viewHolder.image, viewHolder.image?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair2: Pair<View, String> = Pair(viewHolder.cnName, viewHolder.cnName?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair3: Pair<View, String> = Pair(viewHolder.enName, viewHolder.enName?.let { it1 -> ViewCompat.getTransitionName(it1) })
                val pair4: Pair<View, String> = Pair(viewHolder.cnName,"cnTitle")
                val compact = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2,pair3,pair4)
                ActivityCompat.startActivity(activity,intent,compact.toBundle())
            }
        }else if(viewHolder is LoadingViewHolder){
            viewHolder.loading1.visibility = if(isLoading&&!error) View.VISIBLE else View.INVISIBLE
            viewHolder.error.visibility = if(error) View.VISIBLE else View.GONE
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
        if(error)
            error = false
        baikes!!.add(baike)
        notifyDataSetChanged()
        return baikes!!.size - 1
    }

    fun insertBaikeToTop(position:Int,baike:Baike):Int{
        if(baikes==null)
            return -1
        if(error)
            error = false
        baikes!!.add(position,baike)
        notifyDataSetChanged()
        return baikes!!.size - 1
    }

    internal var hideCallback: RecommendConstract.searchCallback? = null
    fun setHideCallback(hideCallback: RecommendConstract.searchCallback) {
        this.hideCallback = hideCallback
    }

    fun setLoadingStatus(isLoading:Boolean){
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    fun isLoading():Boolean{
        return isLoading
    }

    fun setError(boolean: Boolean){
        error = boolean
        notifyDataSetChanged()
    }

    fun isError(boolean: Boolean):Boolean{
        return error
    }
}