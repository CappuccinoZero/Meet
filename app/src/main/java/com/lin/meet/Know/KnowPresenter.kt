package com.lin.meet.Know

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.bean.know_agree
import com.lin.meet.bean.know_comment

class KnowPresenter(view:Constarct.View):Constarct.Presenter{
    private var main:KnowBean ?= null

    override fun onSolve() {
        main!!.solve = true
        main!!.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                view.toast("确认解决")
            }
        })
    }

    override fun onAgree(isAgree: Boolean,floor:Int) {
        val list:ArrayList<BmobQuery<know_agree>> = ArrayList()
        var query = BmobQuery<know_agree>()
        query.addWhereEqualTo("id",id)
        list.add(query)
        query = BmobQuery<know_agree>()
        query.addWhereEqualTo("uid",getUser().uid)
        list.add(query)
        query = BmobQuery<know_agree>()
        query.addWhereEqualTo("floor",floor)
        list.add(query)
        query = BmobQuery<know_agree>()
        query.and(list)
        query.findObjects(object :FindListener<know_agree>(){
            override fun done(p0: MutableList<know_agree>?, p1: BmobException?) {
                if(p1 == null&&p0!!.size>0&&!isAgree){
                    deleteAgree(p0[0])
                }else if(p1 == null&&isAgree){
                    saveAgree(floor)
                }
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

    private fun saveAgree(floor:Int){
        val agree = know_agree()
        agree.floor = floor
        agree.id = id
        agree.uid = uid
        agree.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){

                }
            }
        })
    }

    private fun deleteAgree(agree:know_agree){
        agree.delete(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if(p0 == null){

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
        val comment = know_comment()
        comment.id = id
        comment.uid = getUser().uid
        comment.agree = 0
        comment.content = msg

        view.hideEdit()
        val query = BmobQuery<know_comment>()
        query.addWhereEqualTo("id",id)
        query.findObjects(object :FindListener<know_comment>(){
            override fun done(p0: MutableList<know_comment>?, p1: BmobException?) {
                if(p1 == null){
                    comment.floor = 1 + p0!!.size
                    saveComment(comment)
                }
            }
        })
    }

    private fun saveComment(bean:know_comment){
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.clearEdit()
                    val comment = KnowCommentBean(bean)
                    comment.headUri = getUser().headerUri
                    comment.nickName = getUser().nickName
                    view.insertComment(comment,true)
                }
            }
        })
    }

    @Synchronized
    private fun selectUser(bean:know_comment){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    val comment = KnowCommentBean(bean)
                    comment.headUri = p0!![0].headerUri
                    comment.nickName = p0[0].nickName
                    var position = view.insertComment(comment,false)
                    onAgree(position,bean.floor)
                }
            }
        })
    }


    override fun onInitData(id: String, uid: String) {
        this.id = id
        this.uid = uid
        initUser()
        initMain()
        initComment()
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
        val query = BmobQuery<know_comment>()
        query.addWhereEqualTo("id",id)
        query.order("-agree,-updatedAt")
        query.findObjects(object :FindListener<know_comment>(){
            override fun done(p0: MutableList<know_comment>?, p1: BmobException?) {
                if(p1 == null ){
                    for (index in 0 until p0!!.size){
                        selectUser(p0[index])
                    }
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