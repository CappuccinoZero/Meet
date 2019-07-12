package com.lin.meet.drawer_message.Bean

import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.db_bean.*

class MessageFactory:Reply,Like {
    override fun getParentUid(): String {
        return PUid
    }

    override fun isMain(): Boolean {
        return main
    }

    override fun getSize(): Long {
        return timeSize
    }

    var main:Boolean = false
    var timeSize:Long = 0
    var name = ""
    var header = ""
    var create = ""
    var mainContent = ""
    var reply = ""
    var type = -1
    var imgUri = "@null"
    var ID = ""
    var UID = ""
    var PUid = ""
    override fun getNickname(): String {
        return name
    }

    override fun getHeaderUri(): String {
        return header
    }

    override fun getTime(): String {
        return create
    }

    override fun getContent(): String {
        return mainContent
    }

    override fun getReplyContent(): String {
        return reply
    }

    override fun getImg(): String {
        return imgUri
    }

    override fun getFlag(): Int {
        return type
    }

    override fun getId(): String {
        return ID
    }

    override fun getUid(): String {
        return UID
    }

    //发布的话题被评论
    private constructor(user: User, bean: topic_main, comment: comment){
        initUser(user)
        this.mainContent = "我的话题 : " + getSubString(bean.content,70)
        this.reply = getSubString(comment.content,70)
        this.imgUri = bean.one_uri
        this.ID = bean.id
        this.create = comment.createdAt
        this.type = 1
        this.main = true
        PUid = bean.uid
        setTimeSize(comment.createdAt)
    }

    //发布的视频被评论
    private constructor(user: User, bean: video_main, comment: comment){
        initUser(user)
        this.mainContent = getSubString(bean.tltle,70)
        mainContent = "我的视频 : " + mainContent
        this.reply = getSubString(comment.content,70)
        this.imgUri = bean.uri
        this.ID = bean.id
        this.create = comment.createdAt
        this.type = 2
        this.main = true
        PUid = bean.uid
        setTimeSize(comment.createdAt)
    }

    private constructor(user: User, bean: KnowBean, comment: comment){
        initUser(user)
        this.mainContent = "我的提问 : " +  getSubString(bean.content,70)
        this.reply = getSubString(comment.content,70)
        this.imgUri = bean.img
        this.ID = bean.id
        this.create = comment.createdAt
        this.main = true
        this.type = 3
        PUid = bean.uid
        setTimeSize(comment.createdAt)
    }

    //话题下的评论被评论
    private constructor(user: User, bean: topic_main, comment1: comment,comment2: comment){
        initUser(user)
        this.mainContent = "我的评论 : " +  getSubString(comment1.content,70)
        this.reply = getSubString(comment2.content,70)
        this.imgUri = bean.one_uri
        this.ID = comment1.id
        this.create = comment2.createdAt
        this.type = 1
        PUid = bean.uid
        setTimeSize(comment2.createdAt)
    }

    //推荐下的评论被评论
    private constructor(user: User, bean: recomment_main, comment1: comment, comment2: comment){
        initUser(user)
        this.mainContent = "我的评论 : " +  getSubString(comment1.content,70)
        this.reply = getSubString(comment2.content,70)
        this.imgUri = "@null"
        this.ID = comment1.id
        this.create = comment2.createdAt
        this.type = 0
        PUid = bean.uri
        setTimeSize(comment2.createdAt)
    }

    //视频下的评论被评论
    private constructor(user: User, bean: video_main, comment1: comment, comment2: comment){
        initUser(user)
        this.mainContent = "我的评论 : " +  getSubString(comment1.content,70)
        this.reply = getSubString(comment2.content,70)
        this.imgUri = bean.uri
        this.ID = comment1.id
        this.create = comment2.createdAt
        this.type = 2
        PUid = bean.uid
        setTimeSize(comment2.createdAt)
    }

    private fun initUser(user: User){
        this.name = user.nickName
        this.header = user.headerUri
        this.UID = user.uid
    }

    fun getSubString(str:String,length:Int):String{
        if(str.length > length)
            return str.substring(0,length+1)+"..."
        return str
    }

    fun setTimeSize(str:String){
        val build = StringBuilder()
        for(index in 0 until str.length)
            if(str[index] in '0'..'9')
                build.append(str[index])
        timeSize = build.toString().toLong()
    }

    companion object{
        fun createReply(user: User, bean: topic_main, comment: comment):Reply{
            return MessageFactory(user,bean,comment)
        }
        fun createReply(user: User, bean: video_main, comment: comment):Reply{
            return MessageFactory(user,bean,comment)
        }
        fun createReply(user: User, bean: topic_main, comment1: comment,comment2: comment):Reply{
            return MessageFactory(user,bean,comment1,comment2)
        }
        fun createReply(user: User, bean: recomment_main, comment1: comment, comment2: comment):Reply{
            return MessageFactory(user,bean,comment1,comment2)
        }
        fun createReply(user: User, bean: video_main, comment1: comment, comment2: comment):Reply{
            return MessageFactory(user,bean,comment1,comment2)
        }
        fun createKnow(user: User, bean: KnowBean, comment1: comment):Reply{
            return MessageFactory(user,bean,comment1)
        }

        fun createLike(user: User,bean:topic_main,like: comment_like):Like{
            return MessageFactory(user,bean,like)
        }

        fun createLike(user: User,bean:topic_main,like: comment_like,comment: comment):Like{
            return MessageFactory(user,bean,like,comment)
        }

        fun createLike(user: User,bean:video_main,like: comment_like):Like{
            return MessageFactory(user,bean,like)
        }

        fun createLike(user: User,bean:video_main,like: comment_like,comment: comment):Like{
            return MessageFactory(user,bean,like,comment)
        }

        fun createLike(user: User,bean:KnowBean,like: comment_like,comment: comment):Like{
            return MessageFactory(user,bean,like,comment)
        }

        fun createLike(user: User,bean:recomment_main,like: comment_like,comment: comment):Like{
            return MessageFactory(user,bean,like,comment)
        }
    }

    private constructor(user: User,bean:topic_main,like: comment_like){
        initUser(user)
        this.mainContent = getSubString(bean.content,25)
        this.reply = "赞了我的话题"
        this.ID = bean.id
        this.create = like.createdAt
        this.type = 1
        this.main = true
        PUid = bean.uid
        setTimeSize(like.createdAt)
    }

    private constructor(user: User,bean:topic_main,like: comment_like,comment: comment){
        initUser(user)
        this.mainContent = getSubString(comment.content,25)
        this.reply = "赞了我的评论"
        this.ID = comment.id
        this.create = like.createdAt
        this.type = 1
        this.main = false
        PUid = bean.uid
        setTimeSize(like.createdAt)
    }

    private constructor(user: User,bean: video_main,like: comment_like){
        initUser(user)
        this.mainContent = getSubString(bean.content,25)
        this.reply = "赞了我的视频"
        this.ID = bean.id
        this.create = like.createdAt
        this.type = 2
        this.main = true
        PUid = bean.uid
        setTimeSize(like.createdAt)
    }

    private constructor(user: User,bean:video_main,like: comment_like,comment: comment){
        initUser(user)
        this.mainContent = getSubString(comment.content,25)
        this.reply = "赞了我的评论"
        this.ID = comment.id
        this.create = like.createdAt
        this.type = 2
        this.main = false
        PUid = bean.uid
        setTimeSize(like.createdAt)
    }

    private constructor(user: User,bean:KnowBean,like: comment_like,comment: comment){
        initUser(user)
        this.mainContent = getSubString(comment.content,25)
        this.reply = "赞了我的回答"
        this.ID = bean.id
        this.create = like.createdAt
        this.type = 3
        this.main = false
        PUid = bean.uid
        setTimeSize(like.createdAt)
    }

    private constructor(user: User,bean:recomment_main,like: comment_like,comment: comment){
        initUser(user)
        this.mainContent = getSubString(comment.content,25)
        this.reply = "赞了我的评论"
        this.ID = comment.id
        this.create = like.createdAt
        this.type = 0
        this.main = false
        PUid = bean.uri
        setTimeSize(like.createdAt)
    }
}