package com.lin.meet.recommend

import com.lin.meet.db_bean.Reply
import com.lin.meet.db_bean.comment
import com.lin.meet.jsoup.LoveNewsBean

interface RecommendConstract {
    interface View{
        fun showEdit(postion:Int)
        fun hideEdit()
        fun toast(msg:String)
        fun senMessageResult(resultCode:Int,reply: Reply?)
        fun setThumn(count:Int)
        fun setComment(count:Int)
        fun setCount(thumb:Int,comment:Int)
        fun insertComment(bean: Reply):Int
        fun moveToPosition(position:Int)
        fun sonSendResult(code:Int,position:Int)
        fun like(isLike:Boolean)
        fun star(isStar:Boolean)
        fun likeError()
        fun starError()
        fun setLikeCount(count:Int)
        fun setlike(isLike: Boolean)
        fun setStar(isStar: Boolean)
        fun lickSonResult(resultCode:Int,position:Int,count: Int)
        fun setLikeComment(position:Int,like:Boolean)
    }

    interface Presenter{
        fun checkNet(bean: LoveNewsBean)
        fun onSendMessage(msg:String)
        fun onSendSonMessage(comment: comment, msg:String, position:Int)
        fun onLike(isLike: Boolean)
        fun onStar()
        fun onLikeSon(parentId:String,parentUid:String,like:Boolean)
    }

    interface searchCallback{
        fun setVisiable(visiable: Boolean)
    }
}