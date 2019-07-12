package com.lin.meet.topic

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.TopicMain
import com.lin.meet.bean.User
import com.lin.meet.db_bean.*

class TopicPresenter(view:TopicConstract.View):TopicConstract.Presenter {
    override fun initData(id: String?, isSender: Boolean, parentUid: String) {
        this.parentUid = parentUid
        initData(id,isSender,1)
    }

    var parentUid = ""

    override fun onStar() {
        val starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("id",topicId)
        starQ.addWhereEqualTo("type",1)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                        }
                    })
                }else if(p1==null&&p0!!.size==0){
                    love.createLove(topicId,BmobUser.getCurrentUser(User::class.java).uid,1).save(object :SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
                        }
                    })
                }
            }
        })
    }

    override fun onClickCommentLike(parentId:String,parentUid:String,like:Boolean) {
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
        query4.addWhereEqualTo("mainId",topicId)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",false)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",1)
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
        val like = comment_like.createSonLike(parentUid,parentId,topicId,1)
        like.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
            }
        })
    }


    override fun onClickLike(like: Boolean) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val queryList = ArrayList<BmobQuery<comment_like>>()
        val query1 = BmobQuery<comment_like>()
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        queryList.add(query1)
        val query2 = BmobQuery<comment_like>()
        query2.addWhereEqualTo("parentId",topicId)
        queryList.add(query2)
        val query3 = BmobQuery<comment_like>()
        query3.addWhereEqualTo("parentUid",parentUid)
        queryList.add(query3)
        val query4 = BmobQuery<comment_like>()
        query4.addWhereEqualTo("mainId",topicId)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",true)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",1)
        queryList.add(query6)
        val query = BmobQuery<comment_like>()
        query.and(queryList)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0&&!like){
                    deletelike(p0[0])
                }
                else if(p1==null&&p0!!.size==0&&like){
                    savelike(comment_like.createMainLike(parentUid,topicId,1))
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
                    //
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
                    //
                }
            }
        })
    }

    override fun onSendOnMessage(comment: comment, msg:String, position:Int) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.equals("")){
            view.toast("内容不能为空")
            return
        }
        comment.createSonComment(msg,1).save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null)
                    view.sonSendResult(1,position)
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
        Log.d("测试","???")
        val comment = comment.createMainComment(msg,parentUid,topicId,1)
        comment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    val reply = Reply(comment)
                    reply.headUri  = BmobUser.getCurrentUser(User::class.java).headerUri
                    reply.nickName  = BmobUser.getCurrentUser(User::class.java).nickName
                    view.senMessageResult(1,reply)
                }else{
                    view.senMessageResult(0,null)
                }
            }
        })
    }


    override fun initData(id: String?,sender:Boolean,flag:Int) {
        var query:BmobQuery<topic_main> = BmobQuery()
        topicId = id!!
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    val bean = TopicMain(p0[0])
                    if(sender&& BmobUser.isLogin()||flag==2){
                        val user = BmobUser.getCurrentUser(User::class.java)
                        bean.nickName = user.nickName
                        bean.headerUri = user.headerUri
                        parentUid = user.uid
                        selectComment(bean,2)
                    }
                    else
                        selectComment(bean,1)
                }
                else{
                    view.initResult(-1,null)
                }
            }
        })

        if(!BmobUser.isLogin()) return
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",1)
        like.addWhereEqualTo("parentId",topicId)
        like.addWhereEqualTo("parentUid",parentUid)
        like.addWhereEqualTo("isMain",true)
        like.addWhereEqualTo("mainId",topicId)
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
                    view.setLikeNoAnim(like)
                    view.setThumbCount(p0.size)
                }
            }
        })
        //查询是否<手残
        val starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("id",topicId)
        starQ.addWhereEqualTo("type",1)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0)
                    view.initStart()
            }
        })
    }

    private fun selectComment(bean:TopicMain,code:Int){
        view.initResult(code,bean)
        initComment(topicId)
    }


    //获得评论内容
    private fun initComment(id:String){
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",topicId)
        query.addWhereEqualTo("mainId",topicId)
        query.addWhereEqualTo("main",true)
        query.addWhereEqualTo("flag",1)
        query.addWhereEqualTo("parentUid",parentUid)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(p0: MutableList<comment>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    if(p0.size>0){
                        for(index in 0 until p0.size){
                            getReplyUser(p0[index])
                        }
                        view.setCommentCount(p0.size)
                    }
                    else{

                    }
                }
                else{

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

    private fun getSonReplyCount(reply:Reply){
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",reply.bean.id)
        query.addWhereEqualTo("mainId",topicId)
        query.addWhereEqualTo("main",false)
        query.addWhereEqualTo("flag",1)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(p0: MutableList<comment>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    Log.d("测试",""+p0.size)
                    reply.replyCount = p0.size
                    getSonLikeCount(reply)
                }
            }
        })
    }

    private fun getSonLikeCount(reply:Reply){
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",1)
        like.addWhereEqualTo("parentId",reply.bean.id)
        like.addWhereEqualTo("parentUid",reply.bean.uid)
        like.addWhereEqualTo("isMain",false)
        like.addWhereEqualTo("mainId",topicId)
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


    private val view = view

}