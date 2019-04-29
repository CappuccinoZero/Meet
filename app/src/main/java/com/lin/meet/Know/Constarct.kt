package com.lin.meet.Know

import com.lin.meet.bean.KnowBean

class Constarct {
    interface View{
        fun initAuthor(nickName:String , header:String)
        fun initMain(bean: KnowBean)
        fun toast(msg:String)
        fun insertComment(comment: KnowCommentBean,roll:Boolean):Int
        fun hideEdit()
        fun clearEdit()
        fun updateAgreeCount(position: Int,count:Int)
        fun updateAgree(position: Int)
        fun setSolveVisible(visible:Boolean)
    }

    interface Presenter{
        fun onInitData(id:String,uid:String)
        fun onSendComment(msg:String)
        fun onAgree(isAgree:Boolean,floor:Int)
        fun onSolve()
    }
}