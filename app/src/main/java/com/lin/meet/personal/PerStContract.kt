package com.lin.meet.personal

import android.view.MotionEvent

interface PerStContract {
    interface View{
        fun showLongEdit(msg: String,id:Int)
        fun showShortEdit(msg: String,id:Int)
        fun setHeader(url:String)
        fun setBackground(url:String)
        fun setName(str:String)
        fun getName():String
        fun setSex(str:String)
        fun getSex():String
        fun setBirth(str:String)
        fun getBirth():String
        fun setWork(str:String)
        fun getWork():String
        fun setEmail(str:String)
        fun getEmail():String
        fun setFrom(str:String)
        fun getFrom():String
        fun setUID(str:String)
        fun getUID():String
        fun toast(str:String)
        fun createProgressDialog()
        fun closeProgressDialog()
        fun updateImageView(result: Int,path: String)
        fun updateData(str:String,id:Int)
    }
    interface Model{}
    interface Presenter{
        fun updateBackground(phone:String,token:String,path:String)
        fun canelUpload()
        fun updateHeader(phone:String,token:String,path:String)
        fun onSettingNickName(name:String)
        fun onSettingSex(name:String)
        fun onSettingBirth(name:String)
        fun onSettingWork(name:String)
        fun onSettingEmail(name:String)
        fun onSettingArea(name:String)
        fun onSettingSignature(name:String)
        fun onSettingIntroduce(name:String)
        fun onSettingUID(id:String)
    }
    interface Scroll{
        fun onScrollDown(event: MotionEvent)
    }
}