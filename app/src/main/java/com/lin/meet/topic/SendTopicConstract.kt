package com.lin.meet.topic

interface SendTopicConstract {
    interface View{
        fun setType(type:String)
        fun showLocation(isShow:Boolean)
        fun toast(msg:String)
        fun setSendClickable(isClick:Boolean)
        fun showEmoji()
        fun hideEmoji()
        fun hidePicture(index:Int)
        fun showPicture(index:Int,path:String)
        fun sendResult(ResultCode:Int,id:String)
        fun createProgressDialog()
        fun closeProgressDialog()
    }
    interface Presenter{
        fun checkEdit(title:String,content:String)
        fun closePicture(index: Int)
        fun insertPicture(path:String)
        fun notifiDataChange()
        fun sendTopic(title:String,content:String,type: String,location:String)
        fun canelUpload()
    }

}