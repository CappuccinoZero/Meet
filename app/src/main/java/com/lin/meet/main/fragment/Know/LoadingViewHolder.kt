package com.lin.meet.main.fragment.Know

import android.support.v7.widget.RecyclerView
import android.view.View
import com.lin.meet.R

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val loading1 = itemView.findViewById<View>(R.id.loading_1)
    val error = itemView.findViewById<View>(R.id.errorView)
}