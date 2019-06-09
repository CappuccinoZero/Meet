package com.lin.meet.main.fragment.Home
import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.video_main

class PicturePresenter(mView:PictureContract.View):PictureContract.Presenter {
    override fun insertPictures() {
        insetPictures(limit,false)
    }

    override fun insertPictures(isRefresh: Boolean) {
        insetPictures(initLimit,isRefresh)
    }

    @Synchronized
    private fun insetPictures(limit:Int,isRefresh:Boolean){
        if (loading)return
        loading = true
        val query:BmobQuery<video_main> = BmobQuery()
        query.setLimit(limit)
        query.setSkip(skip)
        query.order("-updatedAt")
        query.addWhereEqualTo("isPicture",true)
        query.findObjects(object : FindListener<video_main>() {
            override fun done(list: MutableList<video_main>?, e: BmobException?) {
                if (isRefresh) view.stopRefresh()
                loading = false
                if (list==null)return
                skip += list.size
                list.shuffle()
                for (index in 0 until list.size){
                    view.insertPictures(list[index])
                    Log.d("测试"+skip,""+list[index].tltle)
                }
            }
        })
    }

    override fun refreshPictures() {
        view.clanPictures()
        skip = 0
        insertPictures(true)
    }

    override fun initPictures() {
        view.clanPictures()
        insertPictures(false)
    }

    private val view:PictureContract.View = mView
    private val limit:Int = 6
    private val initLimit = 12
    private var skip:Int = 0
    private var loading = false
}