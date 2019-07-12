package com.lin.meet.drawer_footprint

import com.lin.meet.bean.MapFlag

interface FootConstract {
    interface View{
        fun toast(msg:String)
        fun insertFlag(flag:MapFlag)
    }
    interface Presenter{
        fun initData()
        fun insertData()
    }
    interface ItemCallback{
        fun selectItem(position:Int)
    }
}