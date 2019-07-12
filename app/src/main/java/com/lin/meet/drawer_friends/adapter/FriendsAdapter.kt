package com.lin.meet.drawer_friends.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.lin.meet.drawer_friends.vc.AttentionFragment
import com.lin.meet.drawer_friends.vc.FansFragment

class FriendsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    private var fragments = ArrayList<Fragment>()
    private val attentionFragment: AttentionFragment = AttentionFragment()
    private val fansFragment: FansFragment = FansFragment()
    init {
        initView()
    }

    fun initView(){
        fragments.add(attentionFragment)
        fragments.add(fansFragment)
    }
    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(i: Int): Fragment? {
        if(i>=fragments.size)
            return null
        return fragments[i]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(position==0)
            return "关注"
        return "粉丝"
    }
}