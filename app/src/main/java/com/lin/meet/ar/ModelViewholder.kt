package com.lin.meet.ar

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lin.meet.R

class ModelViewholder(view: View):RecyclerView.ViewHolder(view) {
    val img = view.findViewById<ImageView>(R.id.img)
    val name = view.findViewById<TextView>(R.id.name)
    val item = view.findViewById<View>(R.id.item)
}