package com.lin.meet.comment

import android.app.Activity
import com.lin.meet.db_bean.Reply

interface ComView{
    fun insertComment(reply: Reply)
    fun insertComment(i:Int,reply: Reply)
    fun refreshData()
    fun setNickName(str:String)
    fun setHeader(str:String)
    fun setTime(str:String)
    fun setLickCount(str:Int)
    fun setLikeStatus(like:Boolean)
    fun setContent(str:String)
    fun CommentCount(count:Int)
    fun toast(msg:String)
    fun sendResult(success:Boolean,content: Reply)
}

interface ComPresenter{
    fun initData(id:String,flag:Int)
    fun likeComment(like:Boolean)
    fun sendComment(content:String)
    fun goToPersonal(activity:Activity)
}