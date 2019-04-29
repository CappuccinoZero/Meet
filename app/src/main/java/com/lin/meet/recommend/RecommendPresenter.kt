package com.lin.meet.recommend

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.*

class RecommendPresenter(view:RecommendConstract.View):RecommendConstract.Presenter {
    @Synchronized
    override fun onLikeSon(floor: Int, position: Int, isLike: Boolean) {
        if(!checkSend("like"))
            return
        var query:BmobQuery<comment_like> = BmobQuery()
        query.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].like = isLike
                    updateSonLike(p0[0],position)
                }
                else if(p1==null&&p0!!.size==0){
                    var like:comment_like = comment_like()
                    like.floor = floor
                    like.uid = BmobUser.getCurrentUser(User::class.java).uid
                    like.uri = uri
                    like.like = isLike
                    saveSonLike(like,position)
                }
            }
        })
    }

    private fun saveSonLike(like:comment_like,position: Int){
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    updateCommentView(position,like.floor)
                }
            }
        })
    }

    private fun updateSonLike(like:comment_like,position: Int){
        like.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    updateCommentView(position,like.floor)
                }
            }
        })
    }

    private fun updateCommentView(position: Int,floor: Int){
        var query:BmobQuery<comment_like> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("floor",floor)
        query.addWhereEqualTo("like",true)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null){
                    view.lickSonResult(1,position,p0!!.size)
                    findSonComment(floor,p0.size)
                }
            }
        })
    }

    private fun findSonComment(floor: Int,count:Int){
        var query:BmobQuery<recommentBean.recomment_comment> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("id",0)
        query.addWhereEqualTo("floor",floor)
        query.findObjects(object :FindListener<recommentBean.recomment_comment>(){
            override fun done(p0: MutableList<recommentBean.recomment_comment>?, p1: BmobException?) {
                if(p1==null){
                    var comment = p0!![0]
                    comment.like = count
                    updateSonComment(comment)
                }
            }
        })
    }

    private fun updateSonComment(comment: recommentBean.recomment_comment){
        comment.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {

            }
        })
    }


    override fun onStar() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        var starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("uri",uri)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    p0[0].delete(object :UpdateListener(){
                        override fun done(p0: BmobException?) {
                        }
                    })
                }else if(p1==null){
                    var love = love()
                    love.uri = uri
                    love.uid = BmobUser.getCurrentUser(User::class.java).uid
                    love.save(object :SaveListener<String>(){
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
        var query:BmobQuery<recommentBean.recomment_islike> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        query.findObjects(object :FindListener<recommentBean.recomment_islike>(){
            override fun done(p0: MutableList<recommentBean.recomment_islike>?, p1: BmobException?) {
                var like:recommentBean.recomment_islike = recommentBean.recomment_islike()
                if(p1==null&&p0!!.size>0){
                    like = p0[0]
                    like.islike = isLike
                    updatelike(like)
                }
                else if(p1==null&&p0!!.size==0){
                    like.uid = BmobUser.getCurrentUser(User::class.java).uid
                    like.uri = uri
                    like.islike = isLike
                    savelike(like)
                }
            }
        })
    }

    @Synchronized
    private fun savelike(like:recommentBean.recomment_islike){
        like.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1!=null){
                    view.likeError()
                    view.toast("点赞失败")
                }else{
                    updateMain()
                }
            }
        })
    }

    @Synchronized
    private fun updatelike(like:recommentBean.recomment_islike){
        like.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0!=null){
                    view.likeError()
                    view.toast("点赞失败")
                }else{
                    updateMain()
                }
            }
        })
    }

    @Synchronized
    private fun updateMain(){
        var likeC:BmobQuery<recommentBean.recomment_islike> = BmobQuery()
        likeC.addWhereEqualTo("uri",uri)
        likeC.addWhereEqualTo("islike",true)
        likeC.findObjects(object :FindListener<recommentBean.recomment_islike>(){
            override fun done(p0: MutableList<recommentBean.recomment_islike>?, p1: BmobException?) {
                if(p1==null){
                    view.setThumn(p0!!.size)
                }
                else{
                    view.setThumn(0)
                }
            }
        })
    }


    private var uri:String = ""
    private val REPLY_DEFAULT = 0
    private val REPLY_MY_MESSAGE = 1

    override fun checkNet(uri: String) {
        this.uri = uri
        var query:BmobQuery<recommentBean.recomment_main> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.findObjects(object : FindListener<recommentBean.recomment_main>() {
            override fun done(list: MutableList<recommentBean.recomment_main>?, e: BmobException?) {
                if(e == null&&list!!.size ==0){
                    insertMainData(uri)
                }
                else if(e ==null&&list!!.size>0){
                    initData(list.get(0))
                }
            }
        })
    }


    @Synchronized
    override fun onSendMessage(msg: String) {
        if(!checkSend(msg))
            return
        var query:BmobQuery<recommentBean.recomment_main> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.findObjects(object : FindListener<recommentBean.recomment_main>() {
            override fun done(list: MutableList<recommentBean.recomment_main>?, e: BmobException?) {
                if(e ==null&&list!!.size>0){
                        senMessage(list.get(0),msg,BmobUser.getCurrentUser(User::class.java))
                }
            }
        })

    }

    @Synchronized
        private fun senMessage(main:recommentBean.recomment_main,msg:String,user:User){
        var comment:recommentBean.recomment_comment = recommentBean.recomment_comment()
        main.comment = main.comment + 1
        comment.uri = this.uri
        comment.content = msg
        comment.floor = main.comment
        comment.uid = user.uid
        comment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    updateMainData(main,comment)
                }else{
                    view.senMessageResult(0)
                }
            }
        })
    }

    @Synchronized
    private fun updateMainData(main:recommentBean.recomment_main,comment:recommentBean.recomment_comment){
        main.update(object : UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    view.senMessageResult(1)
                    insertComment(comment.uri,comment.uid,comment.floor)
                }else{
                    view.senMessageResult(0)
                }
            }
        })
    }

    private fun insertMainData(uri:String){
        var main:recommentBean.recomment_main = recommentBean.recomment_main()
        main.uri = uri
        main.save(object : SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){

                }
            }
        })
    }

    private fun initData(main:recommentBean.recomment_main){
        view.setComment(main.comment)
        var query:BmobQuery<recommentBean.recomment_comment> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("id",0)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<recommentBean.recomment_comment>(){
            override fun done(p0: MutableList<recommentBean.recomment_comment>?, p1: BmobException?) {
                if(p1==null){
                    if(p0!!.size>0){
                        for(index in 0 until p0.size){
                            loadReply(p0[index],REPLY_DEFAULT)
                        }
                    }
                    else{

                    }
                }
                else{

                }
            }
        })
        var like:BmobQuery<recommentBean.recomment_islike> = BmobQuery()
        like.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        like.addWhereEqualTo("uri",uri)
        like.findObjects(object :FindListener<recommentBean.recomment_islike>(){
            override fun done(p0: MutableList<recommentBean.recomment_islike>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.setlike(p0[0].islike)
                }
            }
        })
        var likeC:BmobQuery<recommentBean.recomment_islike> = BmobQuery()
        likeC.addWhereEqualTo("uri",uri)
        likeC.addWhereEqualTo("islike",true)
        likeC.findObjects(object :FindListener<recommentBean.recomment_islike>(){
            override fun done(p0: MutableList<recommentBean.recomment_islike>?, p1: BmobException?) {
                if(p1==null){
                    view.setThumn(p0!!.size)
                }
                else{
                    view.setThumn(0)
                }
            }
        })
        var starQ:BmobQuery<love> = BmobQuery()
        starQ.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        starQ.addWhereEqualTo("uri",uri)
        starQ.findObjects(object :FindListener<love>(){
            override fun done(p0: MutableList<love>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0)
                    view.setStar(true)
            }
        })
    }

    /**
     * 不锁方法有时候会什么都加载不出
     */
    @Synchronized
    private fun loadReply(bean: recommentBean.recomment_comment,flag:Int){
        var query:BmobQuery<User> = BmobQuery()
        var uids:ArrayList<String> = ArrayList()
        uids.add(bean.uid)
        if(bean.comment == 0){
            query.addWhereEqualTo("uid",bean.uid)
        }
        else if(bean.comment == 1){
            uids.add(bean.reply_uid1)
            query.addWhereContainedIn("uid",uids)
        }else{
            uids.add(bean.reply_uid1)
            uids.add(bean.reply_uid2)
            query.addWhereContainedIn("uid",uids)
        }

        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null){
                    var reply = ReplyBean(bean)
                    for (index in 0 until p0!!.size){
                        if(p0[index].uid.equals(bean.uid)){
                            reply.nickName0 = p0[index].nickName
                            reply.header = p0[index].headerUri
                        }
                        if(p0[index].uid.equals(bean.reply_uid1)){
                            reply.nickName1 = p0[index].nickName
                        }
                        if(p0[index].uid.equals(bean.reply_uid2)){
                            reply.nickName2 = p0[index].nickName
                        }
                    }
                    var position=view.insertComment(reply)
                    if(flag==REPLY_MY_MESSAGE){
                        view.moveToPosition(position)
                    }
                    updateUserLikeComment(position,reply.bean)
                }
            }
        })
    }

    private fun updateUserLikeComment(position: Int,bean:recommentBean.recomment_comment){
        var query:BmobQuery<comment_like> = BmobQuery()
        query.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("floor",bean.floor)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    if (p0[0].like)
                        view.setLikeComment(position,true)
                }
            }
        })
    }

    private fun checkSend(msg:String):Boolean{
        if(msg.equals("")){
            view.toast("内容不能为空")
            return false
        }
        var user: User
        if(BmobUser.isLogin())
            user = BmobUser.getCurrentUser(User::class.java)
        else{
            view.toast("用户未登录")
            return false
        }
        return true
    }

    private fun insertComment(uri:String,uid:String,floor:Int){
        var query:BmobQuery<recommentBean.recomment_comment> = BmobQuery()
        query.addWhereEqualTo("uri",uri)
        query.addWhereEqualTo("uid",uid)
        query.addWhereEqualTo("floor",floor)
        query.addWhereEqualTo("id",0)
        query.findObjects(object :FindListener<recommentBean.recomment_comment>(){
            override fun done(p0: MutableList<recommentBean.recomment_comment>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    loadReply(p0[0],REPLY_MY_MESSAGE)
                }
            }
        })
    }

    @Synchronized
    override fun onSendSonMessage(comment: recommentBean.recomment_comment, msg: String,position:Int) {
        var query:BmobQuery<recommentBean.recomment_comment> = BmobQuery()
        query.addWhereEqualTo("uri",comment.uri)
        query.addWhereEqualTo("uid",comment.uid)
        query.addWhereEqualTo("floor",comment.floor)
        query.addWhereEqualTo("id",0)
        query.findObjects(object :FindListener<recommentBean.recomment_comment>(){
            override fun done(p0: MutableList<recommentBean.recomment_comment>?, p1: BmobException?) {
                if(p0!!.size>0&&p1==null){
                    senSonMessage(p0[0],msg,BmobUser.getCurrentUser(User::class.java),position)
                }

            }
        })
    }

    @Synchronized
    private fun senSonMessage(comment: recommentBean.recomment_comment, msg: String,user:User,position:Int){
        var sonComment = recommentBean.recomment_comment()
        sonComment.uid = user.uid
        sonComment.uri = this.uri
        sonComment.floor = comment.floor
        sonComment.id = comment.comment+1
        sonComment.content = msg
        sonComment.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null)
                    updateParentComment(sonComment,comment,position)
            }
        })
    }


    @Synchronized
    private fun updateParentComment(sonComment: recommentBean.recomment_comment,parentComment: recommentBean.recomment_comment,position:Int){
        parentComment.comment = parentComment.comment+1
        var user = BmobUser.getCurrentUser(User::class.java)
        if(parentComment.comment==1){
            parentComment.reply_uid1 = user.uid
            parentComment.content1 = sonComment.content
        }
        else if(parentComment.comment==2){
            parentComment.reply_uid2 = user.uid
            parentComment.content2 = sonComment.content
        }
        parentComment.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    updateView(sonComment,position)
                }
            }
        })
    }

    private fun updateView(sonComment: recommentBean.recomment_comment,position:Int){
        when(sonComment.id){
            1->{
                view.sonSendResult(1,sonComment.content,position)
            }
            2->{
                view.sonSendResult(2,sonComment.content,position)
            }
            else->{
                view.sonSendResult(sonComment.id,sonComment.content,position)
            }
        }
    }

    private val view:RecommendConstract.View = view
}