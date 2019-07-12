package com.lin.meet.ar

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R

class SelectModelAdapter(callback: Callback):RecyclerView.Adapter<ModelViewholder>() {
    private val models = ArrayList<ModelBean>()
    private var context: Context ?= null
    private var callback:Callback ?= callback
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ModelViewholder {
        if(context == null)
            context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.model_view, parent, false)
        return ModelViewholder(view)
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onBindViewHolder(viewholder: ModelViewholder, i: Int) {
        viewholder.img.setImageResource(models[i].id)
        viewholder.name.text = models[i].name
        viewholder.item.setOnClickListener {
            if(callback!=null)
                callback!!.setSelectModel(models[i].model)
        }
    }

    fun addModels(model:ModelBean){
        models.add(model)
    }

    public interface Callback{
        fun setSelectModel(model:String)
    }
}