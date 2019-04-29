package com.lin.meet.main.fragment.Book

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.bean.Baike
import com.lin.meet.encyclopedia.EncyclopediaActivity

class BaikeAdapter: RecyclerView.Adapter<BaikeViewHolder>() {
    private var baikes:ArrayList<Baike> ?= null
    private var context: Context ?= null
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
            context!!.startActivity(intent)
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
}