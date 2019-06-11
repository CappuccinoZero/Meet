package com.lin.meet.picture_observer

import android.content.Intent
import com.lin.meet.bean.User
import com.lin.meet.bean.video_main

interface ObserverContract {
    interface View{
        fun updateAhthor(user: User?,showAttention:Boolean)
        fun toast(msg:String)
        fun setDownloadClickable(clickable:Boolean)
        fun updateBroadcast(intent:Intent)
        fun updateHot()
    }
    interface Presenter{
        fun initAuthorMessage(uid:String)
        fun downloadPicture(uri:String)
        fun updateHot(bean: video_main)
    }
    interface SendView{
        fun toast(msg:String)
        fun SendResult(code:Int,bean: video_main?)
        fun showLoadingDialog()
        fun closeLoadingDialog()
    }
    interface  SendPresenter{
        fun SendPicture(title:String,content:String,path:String)
        fun cancelUpload()
    }
}