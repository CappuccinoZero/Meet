package com.lin.meet.main.fragment.Find

import com.lin.meet.bean.MapFlag
import com.lin.meet.bean.User

interface FindContract {
    interface View{
        fun toast(msg:String)
        fun createProgressDialog()
        fun closeProgressDialog()
        fun SendResult(success: Boolean)
        fun insertFlag(flag: MapFlag,user: User)
    }
    interface Presenter{
        fun onSend(msg:String,image:String,x:Double,y:Double)
        fun cancelUp()
        fun refreshMark()
    }
}