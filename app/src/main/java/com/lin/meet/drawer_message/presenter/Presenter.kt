package com.lin.meet.drawer_message.presenter

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.db_bean.comment
import com.lin.meet.db_bean.recomment_main
import com.lin.meet.db_bean.topic_main
import com.lin.meet.db_bean.video_main
import com.lin.meet.drawer_message.Bean.MessageFactory
import com.lin.meet.drawer_message.MessageConstract

class Presenter(view:MessageConstract.ReplyView):MessageConstract.ReplyPresenter {
    private var user: User?= null
    private val view = view
    private var initCount = 12
    private var limit = 6
    private var skip = 0
    private var loading = false
    override fun refreshData() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        else if (user==null)
            user = BmobUser.getCurrentUser(User::class.java)
        skip = 0
        view.refreshReply()
        initData()
    }

    private fun initData(){
        loading = true
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("parentUid",user?.uid)
        query.addWhereNotEqualTo("uid",user?.uid)
        query.setLimit(initCount)
        query.setSkip(skip)
        query.order("-createAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null){
                    for(index in 0 until list.size){
                        getReplyUser(list[index])
                    }
                    skip += list.size
                }
                loading = false
                view.stopRefresh()
            }
        })
    }

    private fun getReplyUser(bean:comment){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    checkComment(bean,p0[0])
                }
            }
        })
    }

    fun checkComment(bean:comment,user:User){
        if(bean.main){
            when(bean.flag){
                1->{getTopicMain(bean,user)}
                2->{getVideoMain(bean,user)}
                3->{getAnswerMain(bean,user)}
            }
        }else{
            getParentComment(bean,user,bean.flag)
        }
    }

    fun getTopicMain(bean:comment,user:User){
        val query = BmobQuery<topic_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createReply(user,p0[0],bean))
                }
            }
        })
    }

    fun getTopicMain(bean:comment,comment2:comment,user:User){
        val query = BmobQuery<topic_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createReply(user,p0[0],bean,comment2))
                }
            }
        })
    }


    fun getVideoMain(bean:comment,user:User){
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createReply(user,p0[0],bean))
                }
            }
        })
    }

    fun getVideoMain(bean:comment,comment2:comment,user:User){
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createReply(user,p0[0],bean,comment2))
                }
            }
        })
    }

    fun getRecommendMain(bean:comment,comment2:comment,user:User){
        val query = BmobQuery<recomment_main>()
        query.addWhereEqualTo("uri",bean.mainId)
        query.findObjects(object :FindListener<recomment_main>(){
            override fun done(p0: MutableList<recomment_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createReply(user,p0[0],bean,comment2))
                }
            }
        })
    }

    fun getParentComment(bean:comment,user:User,flag:Int){
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("id",bean.parentId)
        query.addWhereEqualTo("flag",flag)
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    when(flag){
                        0->{getRecommendMain(list[0],bean,user)}
                        1->{getTopicMain(list[0],bean,user)}
                        2->{getVideoMain(list[0],bean,user)}
                    }
                }
            }
        })
    }

    fun getAnswerMain(bean:comment,user:User){
        val query = BmobQuery<KnowBean>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertReply(MessageFactory.createKnow(user,p0[0],bean))
                }
            }
        })
    }

    override fun insertData() {
        if(!BmobUser.isLogin()||loading)
            return
        loading = true
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("parentUid",user?.uid)
        query.addWhereNotEqualTo("uid",user?.uid)
        query.setLimit(limit)
        query.setSkip(skip)
        query.order("-createAt")
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null){
                    for(index in 0 until list.size){
                        getReplyUser(list[index])
                    }
                    skip += list.size
                }
                loading = false
                view.stopRefresh()
                view.endLoadMore()
            }
        })
    }
}