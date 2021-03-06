package com.lin.meet.personal

import android.view.MotionEvent

interface PersonalContract {
    interface View{
        fun showEdit()
        fun showAttentionHe()
        fun showAttentioned()
        fun hideAttentionView()
        fun setNumber(str:String)
        fun getNumber():String
        fun setName(str:String)
        fun getName():String
        fun setSex(str:String)
        fun getSex():String
        fun setWork(str:String)
        fun getWork():String
        fun setBirthday(str:String)
        fun getBirthday():String
        fun setEmail(str:String)
        fun getEmail():String
        fun setConstellation(str:String)
        fun getConstellation():String
        fun setFrom(str:String)
        fun getFrom():String
        fun setSignature(str:String)
        fun getSignature():String
        fun setIntroduce(str:String)
        fun getIntroduce():String
        fun setHeader(uri:String)
        fun setAttend(str:String)
        fun getAttend():String
        fun setFans(str:String)
        fun getFans():String
        fun setAge(age:String)
        fun getAge():String
        fun setHeadBg(uri:String)
        fun toast(str:String)
        fun updateImageView(id:Int,path:String)
        fun calculAge(birth:String):String
        fun calculConstellation(birth:String):String
        fun attentionResult(success:Boolean,attention:Boolean)
    }
    interface Model{}
    interface Presenter{
        fun initNetData(uid:String?)
        fun attention()
        fun cancelAttention()
        fun checkFanCount(uid:String?)
        fun checkAttionCount(uid:String?)
        fun checkFanCount()
        fun checkAttionCount()
    }
    interface Scroll{
        fun onScrollDown(event: MotionEvent)
    }
}