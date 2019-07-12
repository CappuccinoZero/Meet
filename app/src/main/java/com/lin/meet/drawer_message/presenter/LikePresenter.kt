package com.lin.meet.drawer_message.presenter

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.db_bean.*
import com.lin.meet.drawer_message.Bean.MessageFactory
import com.lin.meet.drawer_message.MessageConstract

class LikePresenter(view:MessageConstract.LikeView):MessageConstract.LikePresenter {
    val view = view
    private var user: User?= null
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
        view.refreshLike()
        initData()
    }

    private fun initData(){
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        loading = true
        val query = BmobQuery<comment_like>()
        query.addWhereEqualTo("parentUid",user?.uid)
        query.addWhereNotEqualTo("uid",user?.uid)
        query.setLimit(initCount)
        query.setSkip(skip)
        query.order("-createAt")
        query.findObjects(object : FindListener<comment_like>(){
            override fun done(list: MutableList<comment_like>?, e: BmobException?) {
                if(list!=null&&e==null){
                    for(index in 0 until list.size){
                        getReplyUser(list[index])
                    }
                    skip += list.size
                    Log.d("测试点赞",""+list.size)
                }
                loading = false
                view.endLoadMore()
                view.stopRefresh()
            }
        })
    }

    private fun getReplyUser(bean:comment_like){
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

    fun checkComment(bean:comment_like,user:User){
        if(bean.isMain){
            when(bean.flag){
                1->{getTopicMain(bean,user)}
                2->{getVideoMain(bean,user)}
            }
        }else{
            getParentComment(bean,user,bean.flag)
        }
    }

    fun getTopicMain(bean: comment_like, user:User){
        val query = BmobQuery<topic_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean))
                }
            }
        })
    }

    fun getVideoMain(bean: comment_like, user:User){
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean))
                }
            }
        })
    }

    fun getParentComment(bean: comment_like, user:User, flag:Int){
        val query = BmobQuery<comment>()
        query.addWhereEqualTo("id",bean.parentId)
        query.addWhereEqualTo("flag",flag)
        query.findObjects(object :FindListener<comment>(){
            override fun done(list: MutableList<comment>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    when(flag){
                        0->{getRecommendMain(bean,user,list[0])}
                        1->{getTopicMain(bean,user,list[0])}
                        2->{getVideoMain(bean,user,list[0])}
                        3->{getKnowMain(bean,user,list[0])}
                    }
                }
            }
        })
    }

    fun getRecommendMain(bean: comment_like, user:User,comment: comment){
        val query = BmobQuery<recomment_main>()
        query.addWhereEqualTo("uri",bean.mainId)
        query.findObjects(object :FindListener<recomment_main>(){
            override fun done(p0: MutableList<recomment_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean,comment))
                }
            }
        })
    }

    fun getTopicMain(bean: comment_like, user:User,comment: comment){
        val query = BmobQuery<topic_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(p0: MutableList<topic_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean,comment))
                }
            }
        })
    }

    fun getVideoMain(bean: comment_like, user:User,comment: comment){
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean,comment))
                }
            }
        })
    }

    fun getKnowMain(bean: comment_like, user:User,comment: comment){
        val query = BmobQuery<KnowBean>()
        query.addWhereEqualTo("id",bean.mainId)
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p0!=null&&p1==null&&p0.size>0){
                    view.insertLike(MessageFactory.createLike(user,p0[0],bean,comment))
                }
            }
        })
    }

    override fun insertData() {
        if(!BmobUser.isLogin()||loading)
            return
        loading = true
        val query = BmobQuery<comment_like>()
        query.addWhereEqualTo("parentUid",user?.uid)
        query.addWhereNotEqualTo("uid",user?.uid)
        query.setLimit(limit)
        query.setSkip(skip)
        query.order("-createAt")
        query.findObjects(object : FindListener<comment_like>(){
            override fun done(list: MutableList<comment_like>?, e: BmobException?) {
                if(list!=null&&e==null){
                    for(index in 0 until list.size){
                        getReplyUser(list[index])
                    }
                    skip += list.size
                }
                loading = false
                view.endLoadMore()
                view.stopRefresh()
            }
        })
    }
}