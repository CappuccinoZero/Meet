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
        fun setWork(str:String)
        fun getWork():String
        fun setEmail(str:String)
        fun getEmail():String
        fun setConstellation(str:String)
        fun getConstellation():String
        fun setFrom(str:String)
        fun getFrom():String

    }
    interface Model{}
    interface Presenter{}
    interface Scroll{
        fun onScrollDown(event: MotionEvent)
    }
}