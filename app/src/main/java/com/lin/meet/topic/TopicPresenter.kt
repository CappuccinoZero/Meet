package com.lin.meet.topic

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.*

class TopicPresenter(view:TopicConstract.View):TopicConstract.Presenter {
    override fun onStar() {
        var starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",getUser().uid)
        starQ.addWhereEqualTo("id",topicId)
        starQ.addWhereEqualTo("type",1)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                            if(p0 == null)
                                view.onStartResult(1,false)
                        }
                    })
                }else if(p1==null){
                    var love = love()
                    love.id = topicId
                    love.uid = getUser().uid
                    love.type = 1
                    love.save(object :SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
                            if(p0 == null)
                                view.onStartResult(1,true)
                        }
                    })
                }
            }
        })
    }

    override fun onClickCommentLike(floor:Int,postion: Int) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val query:BmobQuery<topic_comment_like> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("uid",getUser().uid)
        query.addWhereEqualTo("level",0)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<topic_comment_like>(){
            override fun done(p0: MutableList<topic_comment_like>?, p1: BmobException?) {
                if(p1 == null && p0!!.size>0){
                    deleteCommentLike(p0[0],postion)
                }
                else if (p1 == null && p0!!.size == 0){
                    insertCommentLike(floor,postion)
                }else{

                }
            }
        })
    }


    private fun deleteCommentLike(like:topic_comment_like,postion: Int){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                    view.likeCommentResult(1,postion,false)
                }
            }
        })
    }

    private fun insertCommentLike(floor: Int,postion: Int){
        val like = topic_comment_like()
        like.floor = floor
        like.uid = getUser().uid
        like.level = 0
        like.id = topicId
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.likeCommentResult(1,postion,true)
                }
            }
        })
    }


    override fun onClickLike() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        var query:BmobQuery<topic_islike> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("uid",getUser().uid)
        query.findObjects(object :FindListener<topic_islike>(){
            override fun done(p0: MutableList<topic_islike>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    deleteLike(p0[0])
                }
                else if (p1==null&&p0!!.size==0){
                    insertLike()
                }
                else{

                }
            }
        })
    }

    private fun deleteLike(like:topic_islike){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                    view.likeResult(1,false)
                }
            }
        })
    }

    private fun insertLike(){
        val like = topic_islike()
        like.id = topicId
        like.islike = true
        like.uid = getUser().uid
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.likeResult(1,true)
                }
            }
        })
    }

    override fun onSendOnMessage(floor: Int, msg: String?, postion: Int) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        var query:BmobQuery<topic_comment> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<topic_comment>(){
            override fun done(p0: MutableList<topic_comment>?, p1: BmobException?) {
                if(p1==null){
                    var len = p0!!.size
                    for(index in 0 until p0.size){
                        if(p0[0].level == 0){
                            p0[0].comment = len
                            if(len == 1){
                                p0[0].content_1 = msg
                                p0[0].uid_1 = getUser().uid
                            }
                            if(len == 2){
                                p0[0].content_2 = msg
                                p0[0].uid_2 = msg
                            }
                            insertSonComment(floor,msg,postion,len)
                            updateComment(p0[0])
                            view.sendSonResult(1,postion,len,msg,getUser().nickName)
                            break
                        }
                    }
                }
                else{
                }
            }
        })
    }

    fun updateComment(comment:topic_comment){
        comment.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                }
            }
        })
    }

    fun insertSonComment(floor: Int, msg: String?, postion: Int,level:Int){
        var comment = topic_comment()
        comment.floor = floor
        comment.content = msg
        comment.uid = getUser().uid
        comment.id = topicId
        comment.level = level
        comment.save(object:SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){

                }
            }
        })
    }

    private var topicId = ""

    override fun senComment(msg: String?) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        var query:BmobQuery<topic_comment> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("level",0)
        query.findObjects(object :FindListener<topic_comment>(){
            override fun done(p0: MutableList<topic_comment>?, p1: BmobException?) {
                if(p1==null){
                    updateComment(msg!!,p0!!.size+1)
                }
                else{
                }
            }
        })
    }

    fun updateComment(msg:String,floor:Int){
        val comment = topic_comment()
        comment.uid = getUser().uid
        comment.id = topicId
        comment.floor = floor
        comment.content = msg
        comment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    insertComment(topicId,getUser().uid,floor)
                }
            }
        })
    }

    fun insertComment(id: String,uid:String,floor:Int){
        var query:BmobQuery<topic_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("uid",uid)
        query.addWhereEqualTo("floor",floor)
        query.addWhereEqualTo("level",0)
        query.findObjects(object :FindListener<topic_comment>(){
            override fun done(p0: MutableList<topic_comment>?, p1: BmobException?) {
                if(p1==null){
                    var reply = ReplyBean(p0!![0])
                    reply.nickName0 = getUser().nickName
                    reply.header = getUser().headerUri
                    reply.commentCount = 0
                    reply.likeCount = 0
                    view.insertComment(reply,true)
                }
            }
        })
    }

    override fun initData(id: String?) {
        var query:BmobQuery<topic_main> = BmobQuery()
        topicId = id!!
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    var bean = TopicMain(p0[0])
                    selectComment(bean)
                }
                else{
                    view.initResult(-1,null)
                }
            }
        })

        if(BmobUser.isLogin()){
            var starQ:BmobQuery<love> = BmobQuery()
            starQ.addWhereEqualTo("uid",getUser().uid)
            starQ.addWhereEqualTo("id",topicId)
            starQ.addWhereEqualTo("type",1)
            starQ.findObjects(object :FindListener<love>(){
                override fun done(p0: MutableList<love>?, p1: BmobException?) {
                    if(p1 == null && p0!!.size>0)
                        view.initStart()
                }
            })
        }
    }


    private fun updateLike(){
        val query:BmobQuery<topic_islike> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("uid",getUser().uid)
        query.addWhereEqualTo("islike",true)
        query.findObjects(object :FindListener<topic_islike>(){
            override fun done(p0: MutableList<topic_islike>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.setLikeNoAnim(true)
                }
            }
        })

    }

    //评论数
    private fun selectComment(bean:TopicMain){
        var query:BmobQuery<topic_comment> = BmobQuery()
        query.addWhereEqualTo("id",bean.bean.id)
        query.addWhereEqualTo("level",0)
        query.findObjects(object :FindListener<topic_comment>(){
            override fun done(p0: MutableList<topic_comment>?, p1: BmobException?) {
                if(p1==null){
                    bean.commentCount = p0!!.size
                    view.setCommentCount(p0.size)
                    selectLike(bean)
                }
                else{
                    view.initResult(-1,null)
                }
            }
        })

    }

    //喜欢数
    private fun selectLike(bean:TopicMain){
        var query:BmobQuery<topic_islike> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.findObjects(object :FindListener<topic_islike>(){
            override fun done(p0: MutableList<topic_islike>?, p1: BmobException?) {
                if(p1==null){
                    bean.likeCount = p0!!.size
                    view.initResult(1,bean)
                    initComment(topicId)
                }else{
                    view.initResult(-1,null)
                }
            }
        })
    }

    //获得评论内容
    private fun initComment(id:String){
        var query:BmobQuery<topic_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("level",0)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<topic_comment>(){
            override fun done(p0: MutableList<topic_comment>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    for(index in 0 until p0.size){
                        initCommentUri(p0[index])
                    }
                }else{

                }
            }
        })
    }

    //获得评论user
    private fun initCommentUri(comment:topic_comment){
        var query:BmobQuery<User> = BmobQuery()
        var uids:ArrayList<String> = ArrayList()
        uids.add(comment.uid)
        if(comment.comment == 0){
            query.addWhereEqualTo("uid",comment.uid)
        }
        else if(comment.comment == 1){
            uids.add(comment.uid_1)
            query.addWhereContainedIn("uid",uids)
        }else{
            uids.add(comment.uid_1)
            uids.add(comment.uid_2)
            query.addWhereContainedIn("uid",uids)
        }
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    var reply = ReplyBean(comment)
                    for(index in 0 until p0.size){
                        if(p0[index].uid == comment.uid){
                            reply.nickName0 = p0[index].nickName
                            reply.header = p0[index].headerUri
                        }
                        else if(p0[index].uid == comment.uid_1){
                            reply.nickName1 = p0[index].nickName
                        }else if(p0[index].uid == comment.uid_2){
                            reply.nickName2 = p0[index].nickName
                        }
                    }
                    val position = view.insertComment(reply,false)
                    getCommentLike(position,reply)
                }
                else{

                }
            }
        })
    }



    //获得每个评论的喜欢个数
    private fun getCommentLike(position:Int,reply:ReplyBean){
        val query:BmobQuery<topic_comment_like> = BmobQuery()
        query.addWhereEqualTo("id",topicId)
        query.addWhereEqualTo("level",0)
        query.addWhereEqualTo("floor",reply.floor)
        query.findObjects(object :FindListener<topic_comment_like>(){
            override fun done(p0: MutableList<topic_comment_like>?, p1: BmobException?) {
                if(p1 == null && p0!!.size>0){
                    view.setCommentLike(position,p0.size)
                    for(index in 0 until p0.size){
                        if(p0[index].uid == getUser().uid){
                            if(!BmobUser.isLogin()){
                                continue
                            }
                            view.setCommentLike(position,true)
                        }
                    }
                }
            }
        })
    }

    private fun getUser():User{
        return BmobUser.getCurrentUser(User::class.java)
    }

    private val view = view

}