package com.lin.meet.ar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lin.meet.R

class ModelFragment():Fragment(), SelectModelAdapter.Callback {
    var callback:Callback ?= null
    public interface Callback{
        fun onClose()
        fun onChange(model:String)
    }
    fun setListener(callback: Callback){
        this.callback = callback
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.model_select_view,container,false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        val back = view.findViewById<ImageView>(R.id.close)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = manager
        recyclerView.adapter = getAdapter()
        back.setOnClickListener { if(callback!=null) callback!!.onClose() }
    }

    private fun getAdapter():SelectModelAdapter{
        val adapter = SelectModelAdapter(this)
        adapter.addModels(ModelBean(resources.getString(R.string.model_rooster),"鸡",R.drawable.chicken))
        adapter.addModels(ModelBean(resources.getString(R.string.model_pig),"猪",R.drawable.pig))
        adapter.addModels(ModelBean(resources.getString(R.string.model_hatDog),"带帽子的狗",R.drawable.hut_dog))
        adapter.addModels(ModelBean(resources.getString(R.string.model_fish),"鱼",R.drawable.fish))
        adapter.addModels(ModelBean(resources.getString(R.string.model_dolphin),"海豚",R.drawable.dolphin))
        adapter.addModels(ModelBean(resources.getString(R.string.model_dog),"秋田犬",R.drawable.dog2))
        adapter.addModels(ModelBean(resources.getString(R.string.model_androidRobot),"机器人",R.drawable.robot))
        return adapter
    }

    override fun setSelectModel(model: String) {
        callback!!.onChange(model)
    }
}