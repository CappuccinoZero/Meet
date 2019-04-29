package com.lin.meet.recommend

import com.lin.meet.bean.ReplyBean
import com.lin.meet.bean.recommentBean

interface RecommendConstract {
    interface View{
        fun showEdit(postion:Int)
        fun hideEdit()
        fun toast(msg:String)
        fun senMessageResult(resultCode:Int)
        fun setThumn(count:Int)
        fun setComment(count:Int)
        fun setCount(thumb:Int,comment:Int)
        fun insertComment(bean:ReplyBean):Int
        fun moveToPosition(position:Int)
        fun sonSendResult(id:Int,msg: String,position:Int)
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
        fun checkNet(uri:String)
        fun onSendMessage(msg:String)
        fun onSendSonMessage(comment: recommentBean.recomment_comment, msg:String,position:Int)
        fun onLike(isLike: Boolean)
        fun onStar()
        fun onLikeSon(floor:Int,position:Int,isLike: Boolean)
    }

    interface Model{

    }
}