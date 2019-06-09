package com.lin.meet.main.fragment.Know

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.transition.Explode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.meet.Know.KnowActivity
import com.lin.meet.R

class KnowAdapter:RecyclerView.Adapter<KnowViewHolder>() {
    private var knows:ArrayList<KnowAndUser> = ArrayList()
    private var context: Context?= null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): KnowViewHolder {
        if(context==null)
            context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.know_view,p0,false)
        return KnowViewHolder(view)
    }

    override fun getItemCount(): Int {
            return knows.size
    }

    override fun onBindViewHolder(viewHolder: KnowViewHolder, i: Int) {
        viewHolder.setHeader(context!!,knows!![i].user.headerUri)
        viewHolder.setImg(context!!,knows!![i].bean.img)
        viewHolder.setContent(knows!![i].bean.content)
        viewHolder.setProblem(knows!![i].bean.solve)
        viewHolder.nickName.text = knows!![i].user.nickName
        viewHolder.item.setOnClickListener {
            val intent = Intent(context, KnowActivity::class.java)
            intent.putExtra("id",knows!![i].bean.id)
            intent.putExtra("uid",knows!![i].user.uid)
            intent.putExtra("img",knows!![i].bean.img)
            if(knows[i].bean.img=="@null"){
                (context as Activity).window.exitTransition = Explode()
                context!!.startActivity(intent)
            }else{
                (context as Activity).window.sharedElementExitTransition = Explode()
                val pair = Pair<View,String>(viewHolder.img,viewHolder.img.transitionName)
                val compact = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,pair)
                ActivityCompat.startActivity(context as Activity,intent,compact.toBundle())
            }
        }
    }

    fun refresh(){
        knows.clear()
        notifyDataSetChanged()
    }

    fun insertKnow(bean:KnowAndUser){
        knows.add(bean)
        notifyDataSetChanged()
    }

    fun insertKnow(position:Int,bean:KnowAndUser){
        knows.add(position,bean)
        notifyDataSetChanged()
    }
}