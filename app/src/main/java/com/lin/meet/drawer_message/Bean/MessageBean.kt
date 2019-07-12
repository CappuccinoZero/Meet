package com.lin.meet.drawer_message.Bean

interface MsgBase{
    fun getSize():Long
    fun getNickname():String
    fun getHeaderUri():String
    fun getTime():String
    fun getImg():String
    fun getFlag():Int
    fun getId():String
    fun getUid():String
    fun isMain():Boolean
    fun getContent():String
    fun getReplyContent():String
    fun getParentUid():String
}
interface Reply:MsgBase
interface Like:MsgBase