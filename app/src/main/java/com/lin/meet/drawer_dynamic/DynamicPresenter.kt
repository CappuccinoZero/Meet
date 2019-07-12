package com.lin.meet.drawer_dynamic

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.lin.meet.db_bean.friends
import com.lin.meet.db_bean.picture_main
import com.lin.meet.db_bean.topic_main
import com.lin.meet.db_bean.video_main

class DynamicPresenter(view:DynamicContract.View): DynamicContract.Presenter {

    override fun onInsertData() {
        insertTopic(limit)
        insertKnow(limit)
        insertPicture(limit)
        insertVideo(limit)
    }

    override fun initNetData() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        view.clearData()
        friends.add(getUser())
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidA",getUser().uid)
        query.findObjects(object :FindListener<friends>(){
            override fun done(list: MutableList<friends>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    initUsers(list.toTypedArray())
                }
            }
        })
    }

    private fun initUsers(uids:Array<friends>){
        skip_t = 0
        skip_k = 0
        skip_v = 0
        skip_p = 0
        loading_topic = false
        loading_know = false
        loading_video = false
        loading_picture = false
        val querys = ArrayList<BmobQuery<User>>()
        for(i in 0 until uids.size){
            val query = BmobQuery<User>()
            query.addWhereEqualTo("uid",uids[i].uidB)
            querys.add(query)
        }
        val query = BmobQuery<User>()
        query.or(querys)
        query.findObjects(object :FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    for(i in 0 until list.size)
                        friends.add(list[i])
                }
                initData()
            }
        })
    }

    private fun initData(){
        insertTopic(initCount)
        insertKnow(initCount)
        insertPicture(initCount)
        insertVideo(initCount)
    }

    private fun insertTopic(limit:Int){
        if(loading_topic)
            return
        loading_topic = true
        val querys = ArrayList<BmobQuery<topic_main>>()
        for(index in 0 until friends.size){
            val query = BmobQuery<topic_main>()
            query.addWhereEqualTo("uid",friends[index].uid)
            querys.add(query)
        }
        val query = BmobQuery<topic_main>()
        query.or(querys)
        query.order("-createAt")
        query.setSkip(skip_t)
        query.setLimit(limit)
        query.findObjects(object :FindListener<topic_main>(){
            override fun done(list: MutableList<topic_main>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    for(index in 0 until list.size){
                        val user = getUserFromArray(list[index].uid)
                        val bean = DynamicBean()
                        bean.header = user.headerUri
                        bean.nickName = user.nickName
                        bean.uid = user.uid
                        bean.setArr(list[index].createdAt)
                        bean.flag = 1
                        bean.id = list[index].id
                        bean.title = list[index].title
                        bean.time = list[index].createdAt
                        bean.img = list[index].one_uri
                        view.insertItem(bean)
                    }
                    skip_t += list.size
                }
                view.endLoadMore()
                view.endRefresh()
                loading_topic = false
            }
        })
    }

    private fun insertVideo(limit:Int){
        if(loading_video)
            return
        loading_video = true
        val querys = ArrayList<BmobQuery<video_main>>()
        for(index in 0 until friends.size){
            val query = BmobQuery<video_main>()
            query.addWhereEqualTo("uid",friends[index].uid)
            querys.add(query)
        }
        val query = BmobQuery<video_main>()
        query.or(querys)
        query.order("-createAt")
        query.setSkip(skip_v)
        query.setLimit(limit)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(list: MutableList<video_main>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    for(index in 0 until list.size){
                        val user = getUserFromArray(list[index].uid)
                        val bean = DynamicBean()
                        bean.header = user.headerUri
                        bean.nickName = user.nickName
                        bean.uid = user.uid
                        bean.setArr(list[index].createdAt)
                        bean.flag = 2
                        bean.id = list[index].id
                        bean.title = list[index].tltle
                        bean.time = list[index].createdAt
                        bean.img = list[index].uri
                        view.insertItem(bean)
                    }
                    skip_v += list.size
                }
                view.endLoadMore()
                view.endRefresh()
                loading_video = false
            }
        })
    }

    private fun insertKnow(limit:Int){
        if(loading_know)
            return
        loading_know = true
        val querys = ArrayList<BmobQuery<KnowBean>>()
        for(index in 0 until friends.size){
            val query = BmobQuery<KnowBean>()
            query.addWhereEqualTo("uid",friends[index].uid)
            querys.add(query)
        }
        val query = BmobQuery<KnowBean>()
        query.or(querys)
        query.order("-createAt")
        query.setSkip(skip_k)
        query.setLimit(limit)
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(list: MutableList<KnowBean>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    for(index in 0 until list.size){
                        val user = getUserFromArray(list[index].uid)
                        val bean = DynamicBean()
                        bean.header = user.headerUri
                        bean.nickName = user.nickName
                        bean.uid = user.uid
                        bean.setArr(list[index].createdAt)
                        bean.flag = 3
                        bean.id = list[index].id
                        bean.title = list[index].content
                        bean.time = list[index].createdAt
                        bean.img = list[index].img
                        view.insertItem(bean)
                    }
                    skip_k += list.size
                }
                view.endLoadMore()
                view.endRefresh()
                loading_know = false
            }
        })
    }

    private fun insertPicture(limit:Int){
        if(loading_picture)
            return
        val querys = ArrayList<BmobQuery<picture_main>>()
        for(index in 0 until friends.size){
            val query = BmobQuery<picture_main>()
            query.addWhereEqualTo("uid",friends[index].uid)
            querys.add(query)
        }
        val query = BmobQuery<picture_main>()
        query.or(querys)
        query.order("-createAt")
        query.setSkip(skip_p)
        query.setLimit(limit)
        query.findObjects(object :FindListener<picture_main>(){
            override fun done(list: MutableList<picture_main>?, e: BmobException?) {
                if(list!=null&&e==null&&list.size>0){
                    for(index in 0 until list.size){
                        val user = getUserFromArray(list[index].uid)
                        val bean = DynamicBean()
                        bean.header = user.headerUri
                        bean.nickName = user.nickName
                        bean.uid = user.uid
                        bean.setArr(list[index].createdAt)
                        bean.flag = 4
                        bean.id = list[index].id
                        bean.title = list[index].tltle
                        bean.time = list[index].createdAt
                        bean.img = list[index].uri
                        view.insertItem(bean)
                    }
                    skip_p += list.size
                }
                view.endLoadMore()
                view.endRefresh()
                loading_picture = false
            }
        })
    }

    private fun getUserFromArray(uid:String):User{
        for(index in 0 until friends.size){
            if(uid == friends[index].uid){
                return friends[index]
            }
        }
        return User()
    }

    private fun getUser(): User {
        return BmobUser.getCurrentUser(User::class.java)
    }

    val friends = ArrayList<User>()
    val view = view
    var skip_t = 0
    var skip_k = 0
    var skip_v = 0
    var skip_p = 0
    var limit = 4
    var initCount = 4
    var loading_topic = false
    var loading_know = false
    var loading_video = false
    var loading_picture = false
}