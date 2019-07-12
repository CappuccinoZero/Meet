package com.lin.meet.know

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.bean.know_agree
import com.lin.meet.db_bean.Reply
import com.lin.meet.db_bean.comment
import com.lin.meet.db_bean.comment_like

class KnowPresenter(view:Constarct.View):Constarct.Presenter{
    private var main:KnowBean ?= null

    override fun onSolve() {
        main!!.solve = true
        main!!.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                view.toast("确认解决")
                view.isChange()
            }
        })
    }

    override fun onAgree(parentId:String,parentUid:String,like:Boolean) {
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
        query4.addWhereEqualTo("mainId",main?.id)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",false)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",3)
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
        val like = comment_like.createSonLike(parentUid,parentId,main?.id,3)
        like.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
            }
        })
    }

    fun onAgree(position: Int,floor:Int) {
        val list:ArrayList<BmobQuery<know_agree>> = ArrayList()
        var query = BmobQuery<know_agree>()
        query.addWhereEqualTo("id",id)
        list.add(query)
        query = BmobQuery<know_agree>()
        query.addWhereEqualTo("floor",floor)
        list.add(query)
        query = BmobQuery<know_agree>()
        query.and(list)
        query.findObjects(object :FindListener<know_agree>(){
            override fun done(p0: MutableList<know_agree>?, p1: BmobException?) {
                if(p1 == null){
                    view.updateAgreeCount(position,p0!!.size)
                    if(BmobUser.isLogin()){
                        for(index in 0 until p0.size){
                            if(p0[index].uid == getUser().uid){
                                view.updateAgree(position)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onSendComment(msg: String) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(msg.isEmpty()){
            view.toast("不能为空")
            return
        }

        val comment = comment.createMainComment(msg,main?.uid,main?.id,3)
        comment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    val reply = Reply(comment)
                    reply.headUri  = BmobUser.getCurrentUser(User::class.java).headerUri
                    reply.nickName  = BmobUser.getCurrentUser(User::class.java).nickName
                    view.insertComment(reply,true)
                    view.commentResult(true)
                }else{
                    view.commentResult(false)
                }
            }
        })
    }

    override fun onInitData(id: String, uid: String) {
        this.id = id
        this.uid = uid
        initUser()
        initMain()
    }

    private fun initUser(){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    view.initAuthor(p0!![0].nickName,p0[0].headerUri)
                }
            }
        })
    }

    private fun initMain(){
        val query = BmobQuery<KnowBean>()
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p1 == null){
                    main = p0!![0]
                    view.initMain(p0[0])
                    checkAuthor(p0[0].uid)
                    initComment()
                }
            }
        })
    }

    private fun checkAuthor(uid:String){
        if(!BmobUser.isLogin())
            return
        if(uid == getUser().uid)
            view.setSolveVisible(true)
    }

    private fun initComment(){
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",main?.id)
        query.addWhereEqualTo("mainId",main?.id)
        query.addWhereEqualTo("main",true)
        query.addWhereEqualTo("flag",3)
        query.addWhereEqualTo("parentUid",uid)
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
                    getSonLikeCount(reply)
                }
            }
        })
    }

    private fun getSonLikeCount(reply:Reply){
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",3)
        like.addWhereEqualTo("parentId",reply.bean.id)
        like.addWhereEqualTo("parentUid",reply.bean.uid)
        like.addWhereEqualTo("isMain",false)
        like.addWhereEqualTo("mainId",main?.id)
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


    private fun getUser():User{
        return BmobUser.getCurrentUser(User::class.java)
    }


    private val view = view
    private var uid = ""
    private var id = ""
}