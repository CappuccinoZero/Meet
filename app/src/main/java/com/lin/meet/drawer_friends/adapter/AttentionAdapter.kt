package com.lin.meet.drawer_friends.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lin.meet.R
import com.lin.meet.bean.User
import com.lin.meet.drawer_friends.FriendHolder
import com.lin.meet.personal.PersonalActivity

class AttentionAdapter: RecyclerView.Adapter<FriendHolder>() {
    private val friends = ArrayList<User>()
    private var context: Context?= null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FriendHolder {
        if(context == null)
            context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.friends_item, viewGroup, false)
        return FriendHolder(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holoder: FriendHolder, i: Int) {
        holoder.nickName.text = friends[i].nickName
        holoder.loadHeader(friends[i].headerUri)
        holoder.loadSignature(friends[i].signature)
        holoder.item.setOnClickListener { PersonalActivity.startOther(context as Activity,friends[i].uid) }
    }

    fun initFriends(){
        friends.clear()
        notifyDataSetChanged()
    }

    fun insertFriends(friend:User){
        friends.add(friend)
        notifyDataSetChanged()
    }

    private fun getTimeArr(str:String):Long{
        val build = StringBuilder()
        for(index in 0 until str.length)
            if(str[index] in '0'..'9')
                build.append(str[index])
        return build.toString().toLong()
    }
}