package com.lin.meet.drawer_collection

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.db_bean.love
import com.lin.meet.db_bean.recomment_main
import com.lin.meet.db_bean.topic_main
import com.lin.meet.db_bean.video_main

class CollPresenter(view:CollContract.View):CollContract.Presenter {
    override fun initNetData() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        val query = BmobQuery<love>()
        query.addWhereEqualTo("uid",getUser().uid)
        query.order("-createAt")
        query.findObjects(object :FindListener<love>(){
            override fun done(list: MutableList<love>?, e: BmobException?) {
                if(e==null&&list!=null){
                    for(index in 0 until list.size)
                        onAssignments(list[index].type,list[index].id)
                }
            }
        })
    }

    fun onAssignments(flag:Int,id:String){
        when(flag){
            0->{
                onGetRecommend(id)
            }
            1->{
                onGetTopic(id)
            }
            2->{
                onGetVideo(id)
            }
            3->{
                onGetKnow(id)
            }
        }
    }

    fun onGetRecommend(id:String){
        val query = BmobQuery<recomment_main>()
        query.addWhereEqualTo("uri",id)
        query.order("-createAt")
        query.findObjects(object :FindListener<recomment_main>(){
            override fun done(list: MutableList<recomment_main>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    val bean = CollBean()
                    bean.id = list[0].uri
                    bean.flag = 0
                    bean.header = ""
                    bean.img = list[0].img
                    bean.time = list[0].time
                    bean.nickName = list[0].author
                    bean.title = list[0].title
                    bean.recommend = list[0].flag
                    view.insertItem(bean)
                }
            }
        })
    }

    fun onGetKnow(id:String){
        val query = BmobQuery<KnowBean>()
        query.addWhereEqualTo("id",id)
        query.order("-createAt")
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(list: MutableList<KnowBean>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    val bean = CollBean()
                    bean.id = id
                    bean.uid = list[0].uid
                    bean.flag = 3
                    bean.time = list[0].createdAt
                    bean.img = list[0].img
                    bean.title = list[0].content
                    onGetUser(bean)
                }
            }
        })
    }

    fun onGetVideo(id:String){
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id",id)
        query.order("-createAt")
        query.findObjects(object :FindListener<video_main>(){
            override fun done(list: MutableList<video_main>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    val bean = CollBean()
                    bean.id = id
                    bean.uid = list[0].uid
                    bean.flag = 2
                    bean.time = list[0].createdAt
                    bean.img = list[0].uri
                    bean.title = list[0].tltle
                    onGetUser(bean)
                }
            }
        })
    }

    fun onGetTopic(id:String){
        val query = BmobQuery<topic_main>()
        query.addWhereEqualTo("id",id)
        query.order("-createAt")
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(list: MutableList<topic_main>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    val bean = CollBean()
                    bean.id = id
                    bean.uid = list[0].uid
                    bean.flag = 1
                    bean.time = list[0].createdAt
                    bean.img = list[0].one_uri
                    bean.title = list[0].title
                    onGetUser(bean)
                }
            }
        })
    }

    fun onGetUser(bean:CollBean){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    bean.header = list[0].headerUri
                    bean.nickName = list[0].nickName
                    view.insertItem(bean)
                }
            }
        })
    }
    private fun getUser(): User {
        return BmobUser.getCurrentUser(User::class.java)
    }

    val view = view
}