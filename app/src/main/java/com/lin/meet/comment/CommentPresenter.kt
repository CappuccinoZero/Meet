package com.lin.meet.comment

import android.app.Activity
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.Reply
import com.lin.meet.db_bean.comment
import com.lin.meet.db_bean.comment_like
import com.lin.meet.personal.PersonalActivity

class CommentPresenter(view:ComView):ComPresenter {
    private var uid = ""
    override fun goToPersonal(activity: Activity) {
        if(uid.isEmpty())
            return
        PersonalActivity.startOther(activity,uid)
    }

    override fun initData(id: String,flag:Int) {
        this.flag = flag
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    mComment = list[0]
                    view.setContent(list[0].content)
                    loadCommentUser(list[0])
                }
            }
        })
        initSonComment(id)
        loadLike(id)
    }

    private fun initSonComment(parentId:String){
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("parentId",parentId)
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    for(index in 0 until list.size){
                        loadSonCommentUser(list[index])
                    }
                    view.CommentCount(list.size)
                }
            }
        })
    }

    private fun loadCommentUser(comment:comment){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",comment.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    view.setNickName(list[0].nickName)
                    view.setHeader(list[0].headerUri)
                }
            }
        })
    }

    private fun loadLike(parentId:String){
        val query = BmobQuery<comment_like>()
        query.addWhereEqualTo("parentId",parentId)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    var like = false
                    if(BmobUser.isLogin()){
                        for(index in 0 until p0.size){
                            if(p0[index].uid==BmobUser.getCurrentUser(User::class.java).uid){
                                like = true
                                break
                            }
                        }
                    }
                    view.setLickCount(p0.size)
                    view.setLikeStatus(like)
                }
            }
        })
    }

    private fun loadSonCommentUser(comment:comment){
        this.uid = comment.uid
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",comment.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    val reply = Reply(comment)
                    reply.nickName = list[0].nickName
                    reply.headUri = list[0].headerUri
                    view.insertComment(reply)
                }
            }
        })
    }


    override fun likeComment(like: Boolean) {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val query:BmobQuery<comment_like> = BmobQuery()
        query.addWhereEqualTo("flag",flag)
        query.addWhereEqualTo("parentId",mComment?.id)
        query.addWhereEqualTo("parentUid",mComment?.uid)
        query.addWhereEqualTo("uid",BmobUser.getCurrentUser(User::class.java).uid)
        query.addWhereEqualTo("isMain",false)
        query.addWhereEqualTo("mainId",mComment?.mainId)
        query.findObjects(object :FindListener<comment_like>(){
            override fun done(p0: MutableList<comment_like>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0&&!like){
                    deletelike(p0[0])
                }
                else if(p1==null&&p0!=null&&p0.size==0&&like){
                    savelike(comment_like.createSonLike(mComment?.uid,mComment?.id,mComment?.mainId,flag))
                }
            }
        })
    }

    @Synchronized
    private fun deletelike(like: comment_like){
        like.delete(object : UpdateListener(){
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
        like.save(object : SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1!=null){
                    view.toast("like error!")
                }else{
                    //
                }
            }
        })
    }

    override fun sendComment(content: String) {
        if(!BmobUser.isLogin()){
            view.toast("用户未登录")
            return
        }
        if(content.isEmpty()){
            view.toast("内容不能为空")
            return
        }
        val son = mComment?.createSonComment(content,flag)
        son?.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    var reply = Reply(son)
                    reply.nickName = BmobUser.getCurrentUser(User::class.java).nickName
                    reply.headUri = BmobUser.getCurrentUser(User::class.java).headerUri
                    view.sendResult(true,reply)
                }
            }
        })
    }


    val view = view
    var flag = -1
    var mComment:comment ?= null
}