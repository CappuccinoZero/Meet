package com.lin.meet.picture_observer

import android.content.Intent
import com.lin.meet.bean.User
import com.lin.meet.db_bean.picture_main

interface ObserverContract {
    interface View{
        fun updateAhthor(user: User?,showAttention:Boolean)
        fun toast(msg:String)
        fun setDownloadClickable(clickable:Boolean)
        fun updateBroadcast(intent:Intent)
        fun updateHot()
        fun setHeader(str:String)
        fun setNickName(str:String)
    }
    interface Presenter{
        fun initAuthorMessage(uid:String)
        fun downloadPicture(uri:String)
        fun updateHot(bean: picture_main)
    }
    interface SendView{
        fun toast(msg:String)
        fun SendResult(code:Int,bean: picture_main?)
        fun showLoadingDialog()
        fun closeLoadingDialog()
    }
    interface  SendPresenter{
        fun SendPicture(title:String,content:String,path:String)
        fun cancelUpload()
    }
}