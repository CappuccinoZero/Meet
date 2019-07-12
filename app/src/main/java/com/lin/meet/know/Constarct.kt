package com.lin.meet.know

import com.lin.meet.bean.KnowBean
import com.lin.meet.db_bean.Reply

class Constarct {
    interface View{
        fun initAuthor(nickName:String , header:String)
        fun initMain(bean: KnowBean)
        fun toast(msg:String)
        fun insertComment(comment: Reply, roll:Boolean):Int
        fun hideEdit()
        fun clearEdit()
        fun commentResult(success:Boolean)
        fun updateAgreeCount(position: Int,count:Int)
        fun updateAgree(position: Int)
        fun setSolveVisible(visible:Boolean)
        fun isChange()
    }

    interface Presenter{
        fun onInitData(id:String,uid:String)
        fun onSendComment(msg:String)
        fun onAgree(parentId:String,parentUid:String,like:Boolean)
        fun onSolve()
    }
}