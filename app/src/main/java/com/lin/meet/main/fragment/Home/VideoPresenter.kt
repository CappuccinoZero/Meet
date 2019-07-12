package com.lin.meet.main.fragment.Home

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.video_main

class VideoPresenter(view:HomeConstract.VideoView):HomeConstract.VideoPresenter {
    override fun onInsertToTop() {
        if (loading)return
        loading = true
        view.setNetError(false)
        val query = BmobQuery<video_main>()
        query.order("-updatedAt")
        query.addWhereEqualTo("isPicture",false)
        query.setLimit(page)
        query.setSkip(allPage)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if (p1 == null && p0!=null) {
                    view.setNetError(false)
                    allPage += p0.size
                    for (i in p0.indices) {
                        val bean = VideoBean(p0[i])
                        onLoadUserTop(bean, i)
                    }
                }else if(p1!=null){
                    view.setNetError(true)
                }
                view.endLoadMore()
                view.endRefresh()
                loading = false
            }
        })
    }

    private var allPage = 0//已经加载数量
    private val page = 10//每次加载的数量
    private var loading = false
    override fun onInitVideos(flag: Int) {//0,default 1,id,2,refresh
        allPage = 0
        view.refreshVideos()
        onLoadVideos(flag)
    }

    override fun onInsertVideos() {
        onLoadVideos(0)
    }

    override fun onInsertVideo(id: String?) {
        val query = BmobQuery<video_main>()
        query.addWhereEqualTo("id", id)

    }

    val view = view

    private fun onLoadVideos(flag: Int) {
        if (loading)return
        loading = true
        view.setNetError(false)
        val query = BmobQuery<video_main>()
        query.order("-updatedAt")
        query.setLimit(page)
        query.setSkip(allPage)
        query.findObjects(object :FindListener<video_main>(){
            override fun done(p0: MutableList<video_main>?, p1: BmobException?) {
                if (p1 == null && p0!=null) {
                    view.setNetError(false)
                    allPage += p0.size
                    for (i in p0.indices) {
                        val bean = VideoBean(p0[i])
                        onLoadUser(bean, flag)
                    }
                }else if(p1!=null&&flag==2){
                    view.setNetError(true)
                }
                view.endLoadMore()
                view.endRefresh()
                loading = false
            }
        })
    }

    private fun onLoadUser(bean: VideoBean, flag: Int) {
        if(bean==null) return
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid", bean.bean.uid)
        query.findObjects(object : FindListener<User>() {
            override fun done(list: List<User>, e: BmobException?) {
                if (e == null && list.size > 0) {
                    bean.nickName = list[0].nickName
                    bean.headerUri = list[0].headerUri
                    if (flag == 1) {
                        view.insertVideo(0, bean)
                    } else {
                        view.insertVideo(bean)
                    }
                }
            }
        })
    }

    private fun onLoadUserTop(bean: VideoBean, position: Int) {
        if(bean==null) return
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid", bean.bean.uid)
        query.findObjects(object : FindListener<User>() {
            override fun done(list: List<User>, e: BmobException?) {
                if (e == null && list.size > 0) {
                    bean.nickName = list[0].nickName
                    bean.headerUri = list[0].headerUri
                    view.insertVideo(position, bean)
                }
            }
        })
    }

}