package com.lin.meet.picture_observer

import com.lin.meet.bean.User

interface ObserverContract {
    interface View{
        fun updateAhthor(user: User?,showAttention:Boolean)
        fun toast(msg:String)
        fun setDownloadClickable(clickable:Boolean)
    }
    interface Presenter{
        fun initAuthorMessage(uid:String)
        fun downloadPicture()
    }
}