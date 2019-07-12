package com.lin.meet.drawer_message.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.lin.meet.drawer_message.view.LikeFragment
import com.lin.meet.drawer_message.view.ReplyFragment

class MessageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    private var fragments = ArrayList<Fragment>()
    private val replyFragment: ReplyFragment = ReplyFragment()
    private val likeFragment: LikeFragment = LikeFragment()
    init {
        initView()
    }

    fun initView(){
        fragments.add(replyFragment)
        fragments.add(likeFragment)
    }
    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(i: Int): Fragment? {
        if(i>=fragments.size)
            return null
        return fragments[i]
    }
}