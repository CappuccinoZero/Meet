package com.lin.meet.drawer_message

import com.lin.meet.drawer_message.Bean.Like
import com.lin.meet.drawer_message.Bean.Reply

interface MessageConstract{
    interface viewBase{
        fun toast(msg:String)
        fun stopRefresh()
        fun endLoadMore()
    }
    interface ReplyView:viewBase{
        fun refreshReply()
        fun insertReply(reply: Reply)
    }
    interface ReplyPresenter{
        fun refreshData()
        fun insertData()
    }

    interface LikeView:viewBase{
        fun refreshLike()
        fun insertLike(like: Like)
    }
    interface LikePresenter:ReplyPresenter
}