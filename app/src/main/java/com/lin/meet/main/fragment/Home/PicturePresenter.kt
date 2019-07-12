package com.lin.meet.main.fragment.Home
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.db_bean.picture_main

class PicturePresenter(mView:PictureContract.View):PictureContract.Presenter {
    override fun insertToTop() {
        insetPictures(initLimit,true,true)
    }

    override fun insertPictures() {
        insetPictures(limit,false,false)
    }

    override fun insertPictures(isRefresh: Boolean) {
        insetPictures(initLimit,isRefresh,false)
    }

    @Synchronized
    private fun insetPictures(limit:Int,isRefresh:Boolean,top:Boolean){
        if (loading)return
        loading = true
        view.setNetError(false)
        val query:BmobQuery<picture_main> = BmobQuery()
        query.setLimit(limit)
        query.setSkip(skip)
        query.order("-updatedAt")
        query.findObjects(object : FindListener<picture_main>() {
            override fun done(list: MutableList<picture_main>?, e: BmobException?) {
                view.stopRefresh()
                view.endLoadMore()
                if(e!=null){
                    if(isRefresh)
                        view.setNetError(true)
                    return
                }
                view.setNetError(false)
                loading = false
                if (list==null)return
                skip += list.size
                list.shuffle()
                for (index in 0 until list.size){
                    if(!top)
                        view.insertPictures(list[index])
                    else
                        view.insertPictures(index,list[index])
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
        insertPictures(true)
    }

    private val view:PictureContract.View = mView
    private val limit:Int = 12
    private val initLimit = 18
    private var skip:Int = 0
    private var loading = false
}