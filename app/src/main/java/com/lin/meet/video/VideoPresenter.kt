package com.lin.meet.video

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.*

class VideoPresenter(view:VideoContract.View):VideoContract.presenter{
    override fun initData(id: String?) {
        this.id = id!!
        var query: BmobQuery<video_main> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.playVideo(p0[0].uri,p0[0].tltle)
                    initAuthor(p0[0].uid)
                }else{
                    view.toast("视频获取失败")
                }
            }
        })
    }

    private fun initAuthor(uid:String){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid", uid)
        query.findObjects(object : FindListener<User>() {
            override fun done(list: List<User>, e: BmobException?) {
                if (e == null&&list.size>0) {
                    view.setNickName(list[0].nickName)
                    view.setHeader(list[0].headerUri)
                } else {
                    view.toast("用户信息获取失败")
                }
            }
        })
        updateLikeAndStar()
        initComment()
    }

    private fun initComment(){
        var query:BmobQuery<video_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("level",0)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<video_comment>(){
            override fun done(p0: MutableList<video_comment>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    for(index in 0 until p0.size){
                        initCommentUri(p0[index])
                    }
                }else{

                }
            }
        })
    }

    @Synchronized
    private fun initCommentUri(comment:video_comment){
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

    private fun getCommentLike(position:Int,reply:ReplyBean){
        if(!BmobUser.isLogin())
            return
        val query:BmobQuery<video_comment_like> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("level",0)
        query.addWhereEqualTo("floor",reply.floor)
        query.findObjects(object :FindListener<video_comment_like>(){
            override fun done(p0: MutableList<video_comment_like>?, p1: BmobException?) {
                if(p1 == null && p0!!.size>0){
                    view.setCommentLikeCount(position,p0.size)
                    for(index in 0 until p0.size){
                        if(p0[index].uid == getUser().uid){
                            view.setCommentLike(position,true)
                        }
                    }
                }
            }
        })
    }


    private fun updateLikeAndStar(){
        if(!BmobUser.isLogin())
            return
        val query:BmobQuery<video_like> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("uid",getUser().uid)
        query.findObjects(object :FindListener<video_like>(){
            override fun done(p0: MutableList<video_like>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.likeResult(1,true)
                }
            }
        })

        var starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",getUser().uid)
        starQ.addWhereEqualTo("id",id)
        starQ.addWhereEqualTo("type",2)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.onStarResult(1,true)
                }
            }
        })

    }

    override fun senComment(msg: String?) {
        if(!BmobUser.isLogin()){
            view.toast("需要登录才能评论")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        var query:BmobQuery<video_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("level",0)
        query.findObjects(object :FindListener<video_comment>(){
            override fun done(p0: MutableList<video_comment>?, p1: BmobException?) {
                if(p1 == null){
                    updateComment(msg!!,p0!!.size+1)
                }
            }
        })
    }

    fun updateComment(comment: video_comment){
        comment.update(object : UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                }
            }
        })
    }

    fun updateComment(msg:String,floor:Int){
        val comment = video_comment()
        comment.uid = getUser().uid
        comment.id = id
        comment.floor = floor
        comment.content = msg
        comment.save(object : SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    insertComment(id,getUser().uid,floor)
                }
            }
        })
    }

    fun insertComment(id: String,uid:String,floor:Int){
        var query:BmobQuery<video_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("uid",uid)
        query.addWhereEqualTo("floor",floor)
        query.addWhereEqualTo("level",0)
        query.findObjects(object :FindListener<video_comment>(){
            override fun done(p0: MutableList<video_comment>?, p1: BmobException?) {
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

    override fun onSendOnMessage(floor: Int, msg: String?, postion: Int) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        var query:BmobQuery<video_comment> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<video_comment>(){
            override fun done(p0: MutableList<video_comment>?, p1: BmobException?) {
                if(p1==null){
                    val len = p0!!.size
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

    fun insertSonComment(floor: Int, msg: String?, postion: Int,level:Int){
        var comment = video_comment()
        comment.floor = floor
        comment.content = msg
        comment.uid = getUser().uid
        comment.id = id
        comment.level = level
        comment.save(object:SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){

                }
            }
        })
    }

    override fun onClickLike() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        var query:BmobQuery<video_like> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("uid",getUser().uid)
        query.findObjects(object :FindListener<video_like>(){
            override fun done(p0: MutableList<video_like>?, p1: BmobException?) {
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

    private fun deleteLike(like: video_like){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                    view.likeResult(1,false)
                }
            }
        })
    }

    private fun insertLike(){
        val like = video_like()
        like.id = id
        like.uid = getUser().uid
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.likeResult(1,true)
                }
            }
        })
    }

    override fun onClickCommentLike(floor: Int, position: Int) {
        val query:BmobQuery<video_comment_like> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.addWhereEqualTo("uid",getUser().uid)
        query.addWhereEqualTo("level",0)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<video_comment_like>(){
            override fun done(p0: MutableList<video_comment_like>?, p1: BmobException?) {
                if(p1 == null && p0!!.size>0){
                    deleteCommentLike(p0[0],position)
                }
                else if (p1 == null && p0!!.size == 0){
                    insertCommentLike(floor,position)
                }else{

                }
            }
        })
    }

    private fun deleteCommentLike(like:video_comment_like,postion: Int){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){
                    view.likeCommentResult(1,postion,false)
                }
            }
        })
    }

    private fun insertCommentLike(floor: Int,postion: Int){
        val like = video_comment_like()
        like.floor = floor
        like.uid = getUser().uid
        like.level = 0
        like.id = id
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.likeCommentResult(1,postion,true)
                }
            }
        })
    }

    override fun onStar() {
        var starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",getUser().uid)
        starQ.addWhereEqualTo("id",id)
        starQ.addWhereEqualTo("type",2)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                            if(p0 == null)
                                view.onStarResult(1,false)
                        }
                    })
                }else if(p1==null){
                    var love = love()
                    love.id = id
                    love.uid = getUser().uid
                    love.type = 2
                    love.save(object :SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
                            if(p0 == null)
                                view.onStarResult(1,true)
                        }
                    })
                }
            }
        })
    }

    private fun getUser():User{
        return BmobUser.getCurrentUser(User::class.java)
    }

    val view = view
    var id:String = ""

}