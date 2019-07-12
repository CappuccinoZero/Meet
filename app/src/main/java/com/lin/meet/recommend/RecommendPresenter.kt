package com.lin.meet.recommend

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.*
import com.lin.meet.jsoup.LoveNewsBean

class RecommendPresenter(view:RecommendConstract.View):RecommendConstract.Presenter {
    override fun onLikeSon(parentId: String, parentUid:String, like: Boolean) {
        if(!checkSend("like"))
            return
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
        query4.addWhereEqualTo("mainId",uri)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",false)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",0)
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
        val like = comment_like.createSonLike(parentUid,parentId,uri,0)
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
        starQ.addWhereEqualTo("id",uri)
        starQ.addWhereEqualTo("type",0)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                        }
                    })
                }else if(p1==null&&p0!!.size==0){
                    love.createLove(uri,BmobUser.getCurrentUser(User::class.java).uid,0).save(object :SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
                        }
                    })
                }
            }
        })
    }

    override fun onLike(isLike: Boolean) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val queryList = ArrayList<BmobQuery<comment_like>>()
        val query1 = BmobQuery<comment_like>()
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        queryList.add(query1)
        val query2 = BmobQuery<comment_like>()
        query2.addWhereEqualTo("parentId",uri)
        queryList.add(query2)
        val query3 = BmobQuery<comment_like>()
        query3.addWhereEqualTo("parentUid",parentUid)
        queryList.add(query3)
        val query4 = BmobQuery<comment_like>()
        query4.addWhereEqualTo("mainId",uri)
        queryList.add(query4)
        val query5 = BmobQuery<comment_like>()
        query5.addWhereEqualTo("isMain",true)
        queryList.add(query5)
        val query6 = BmobQuery<comment_like>()
        query6.addWhereEqualTo("flag",0)
        queryList.add(query6)
        val query = BmobQuery<comment_like>()
        query.and(queryList)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0&&!isLike){
                    deletelike(p0[0])
                }
                else if(p1==null&&p0!!.size==0&&isLike){
                    savelike(comment_like.createMainLike(parentUid,uri,0))
                }
            }
        })
    }

    @Synchronized
    private fun savelike(like: comment_like){
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1!=null){
                    view.likeError()
                    view.toast("like error!")
                }else{
                    //like success
                }
            }
        })
    }

    @Synchronized
    private fun deletelike(like: comment_like){
        like.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0!=null){
                    view.likeError()
                    view.toast("点赞失败")
                }else{
                    // like success
                }
            }
        })
    }


    private var uri:String = ""
    private val parentUid:String = "@null"

    override fun checkNet(bean: LoveNewsBean) {
        this.uri = bean.contentUri
        val query:BmobQuery<recomment_main> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.findObjects(object : FindListener<recomment_main>() {
            override fun done(list: MutableList<recomment_main>?, e: BmobException?) {
                if(e == null&&list!!.size ==0){
                    insertMainData(bean)
                }
                else if(e ==null&&list!!.size>0){
                    view.setComment(list[0].comment)
                    initData()
                }
            }
        })
    }


    @Synchronized
    override fun onSendMessage(msg: String) {
        if(!checkSend(msg))
            return
        val comment = comment.createMainComment(msg,parentUid,uri,0)
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


    private fun insertMainData(bean: LoveNewsBean){
        var main:recomment_main = recomment_main()
        main.uri = bean.contentUri
        main.img = bean.img
        main.title = bean.title
        main.time = bean.time
        main.flag = bean.flag
        main.author = bean.author
        main.save(object : SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                }
            }
        })
    }

    //查询父评论
    private fun initData(){
        val query:BmobQuery<comment> = BmobQuery()
        query.addWhereEqualTo("parentId",uri)
        query.addWhereEqualTo("mainId",uri)
        query.addWhereEqualTo("main",true)
        query.addWhereEqualTo("flag",0)
        query.addWhereEqualTo("parentUid",parentUid)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(p0: MutableList<comment>?, p1: BmobException?) {
                if(p1==null&&p0!=null){
                    if(p0.size>0){
                        for(index in 0 until p0.size){
                            getReplyUser(p0[index])
                        }
                        view.setComment(p0.size)
                    }
                    else{

                    }
                }
                else{

                }
            }
        })
        //查询是否赞
        if(!BmobUser.isLogin()) return
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",0)
        like.addWhereEqualTo("parentId",uri)
        like.addWhereEqualTo("parentUid",parentUid)
        like.addWhereEqualTo("isMain",true)
        like.addWhereEqualTo("mainId",uri)
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
                    view.setlike(like)
                    view.setThumn(p0.size)
                }
            }
        })
        //查询是否<手残
        val starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("id",uri)
        starQ.addWhereEqualTo("type",0)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0)
                    view.setStar(true)
            }
        })
    }


    //获得评论用户信息
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
        query.addWhereEqualTo("mainId",uri)
        query.addWhereEqualTo("main",false)
        query.addWhereEqualTo("flag",0)
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

    //更新子赞数
    private fun getSonLikeCount(reply:Reply){
        val like:BmobQuery<comment_like> = BmobQuery()
        like.addWhereEqualTo("flag",0)
        like.addWhereEqualTo("parentId",reply.bean.id)
        like.addWhereEqualTo("parentUid",reply.bean.uid)
        like.addWhereEqualTo("isMain",false)
        like.addWhereEqualTo("mainId",uri)
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
                    view.insertComment(reply)
                }
            }
        })
    }

    private fun checkSend(msg:String):Boolean{
        if(msg.equals("")){
            view.toast("内容不能为空")
            return false
        }
        val user: User
        if(BmobUser.isLogin())
            user = BmobUser.getCurrentUser(User::class.java)
        else{
            view.toast("用户未登录")
            return false
        }
        return true
    }


    @Synchronized
    override fun onSendSonMessage(comment: comment, msg: String,position:Int) {
        senSonMessage(comment,msg,BmobUser.getCurrentUser(User::class.java),position)
    }

    @Synchronized
    private fun senSonMessage(comment: comment, msg: String,user:User,position:Int){
        comment.createSonComment(msg,0).save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null)
                    view.sonSendResult(1,position)
            }
        })
    }

    private val view:RecommendConstract.View = view
}