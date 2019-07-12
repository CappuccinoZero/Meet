package com.lin.meet.video

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.*

class VideoPresenter(view:VideoContract.View):VideoContract.presenter{
    override fun initData(id: String?,uid: String?) {
        this.parentUid = uid!!
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
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",id)
        query.addWhereEqualTo("mainId",id)
        query.addWhereEqualTo("main",true)
        query.addWhereEqualTo("flag",2)
        query.addWhereEqualTo("parentUid",parentUid)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(p0: MutableList<comment>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    if(p0.size>0){
                        for(index in 0 until p0.size){
                            getReplyUser(p0[index])
                        }
                    }
                    else{

                    }
                }
                else{

                }
                if(p0==null||p0.size==0){
                    view.setNullCommentVisiable(true)
                }
            }
        })
    }

    @Synchronized
    private fun getReplyUser(bean: comment){
        val query:BmobQuery<User> = BmobQuery()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    val reply = Reply(bean)
                    reply.nickName = p0[0].nickName
                    reply.headUri = p0[0].headerUri
                    getSonReplyCount(reply)
                }
            }
        })
    }

    //获得字评论数目
    private fun getSonReplyCount(reply:Reply){
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",reply.bean.id)
        query.addWhereEqualTo("mainId",id)
        query.addWhereEqualTo("main",false)
        query.addWhereEqualTo("flag",2)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(p0: MutableList<comment>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    reply.replyCount = p0.size
                    getSonLikeCount(reply)
                }
            }
        })
    }

    private fun getSonLikeCount(reply:Reply){
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",2)
        like.addWhereEqualTo("parentId",reply.bean.id)
        like.addWhereEqualTo("parentUid",reply.bean.uid)
        like.addWhereEqualTo("isMain",false)
        like.addWhereEqualTo("mainId",id)
        like.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    reply.likeCount = p0.size
                    var like = false
                    if(BmobUser.isLogin()){
                        for(index in 0 until p0.size)
                            if(p0[index].uid == BmobUser.getCurrentUser(User::class.java).uid){
                                like = true
                                break
                            }
                    }
                    reply.like = like
                    view.insertComment(reply,false)
                }
            }
        })
    }





    private fun updateLikeAndStar(){
        if(!BmobUser.isLogin())
            return

        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",2)
        like.addWhereEqualTo("parentId",id)
        like.addWhereEqualTo("parentUid",parentUid)
        like.addWhereEqualTo("isMain",true)
        like.addWhereEqualTo("mainId",id)
        like.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    var like = false
                    for (index in 0 until p0.size){
                        if(p0[index].uid == BmobUser.getCurrentUser(User::class.java).uid){
                            like = true
                            break
                        }
                    }
                    view.likeResult(1,like)
                }
            }
        })
        //查询是否<手残
        val starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("id",id)
        starQ.addWhereEqualTo("type",2)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0)
                    view.onStarResult(1,true)
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
        val comment = comment.createMainComment(msg,parentUid,id,2)
        comment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    val reply = Reply(comment)
                    reply.headUri  = BmobUser.getCurrentUser(User::class.java).headerUri
                    reply.nickName  = BmobUser.getCurrentUser(User::class.java).nickName
                    view.senMessageResult(1,reply)
                    view.setNullCommentVisiable(false)
                }else{
                    view.senMessageResult(0,null)
                }
            }
        })
    }


    override fun onSendSonMessage(comment: comment, msg: String,position:Int) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        comment.createSonComment(msg,2).save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    view.sonSendResult(1,position)
                    view.setNullCommentVisiable(false)
                }
            }
        })
    }


    override fun onClickLike(isLike: Boolean) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val queryList = ArrayList<BmobQuery<comment_like>>()
        val query1 = BmobQuery<comment_like>()
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        queryList.add(query1)
        val query2 = BmobQuery<comment_like>()
        query2.addWhereEqualTo("parentId",id)
        queryList.add(query2)
        val query3 = BmobQuery<comment_like>()
        query3.addWhereEqualTo("parentUid",parentUid)
        queryList.add(query3)
        val query4 = BmobQuery<comment_like>()
        query4.addWhereEqualTo("mainId",id)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",true)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",2)
        queryList.add(query6)
        val query = BmobQuery<comment_like>()
        query.and(queryList)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0&&!isLike){
                    deletelike(p0[0])
                }
                else if(p1==null&&p0!!.size==0&&isLike){
                    savelike(comment_like.createMainLike(parentUid,id,2))
                }
            }
        })
    }

    @Synchronized
    private fun deletelike(like: comment_like){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0!=null){
                    view.toast("点赞失败")
                }else{
                    // like success
                }
            }
        })
    }

    @Synchronized
    private fun savelike(like: comment_like){
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1!=null){
                    view.toast("like error!")
                }else{
                    //like success
                }
            }
        })
    }

    override fun onClickCommentLike(parentId: String, parentUid:String, like: Boolean) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val queryList = ArrayList<BmobQuery<comment_like>>()
        val query1 = BmobQuery<comment_like>()
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        queryList.add(query1)
        val query2 = BmobQuery<comment_like>()
        query2.addWhereEqualTo("parentId",parentId)
        queryList.add(query2)
        val query3 = BmobQuery<comment_like>()
        query3.addWhereEqualTo("parentUid",parentUid)
        queryList.add(query3)
        val query4 = BmobQuery<comment_like>()
        query4.addWhereEqualTo("mainId",id)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",false)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",2)
        queryList.add(query6)
        val query = BmobQuery<comment_like>()
        query.and(queryList)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p0!=null&&p0.size>0&&!like){
                    deleteSonLike(p0[0])
                }else if(p0!=null&&like&&p0.size==0){
                    insertSonLike(parentId,parentUid)
                }
            }
        })
    }

    @Synchronized
    private fun deleteSonLike(like: comment_like){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
            }
        })
    }

    @Synchronized
    private fun insertSonLike(parentId: String, parentUid:String){
        val like = comment_like.createSonLike(parentUid,parentId,id,2)
        like.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
            }
        })
    }



    override fun onStar() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }

        val starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("id",id)
        starQ.addWhereEqualTo("type",2)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                        }
                    })
                }else if(p1==null&&p0!!.size==0){
                    love.createLove(id,BmobUser.getCurrentUser(User::class.java).uid,2).save(object :SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
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
    var parentUid = ""
}