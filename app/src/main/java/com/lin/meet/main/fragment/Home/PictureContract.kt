package com.lin.meet.main.fragment.Home

import com.lin.meet.db_bean.picture_main

interface PictureContract {
    interface View{
        fun insertPictures(bean: picture_main)
        fun insertPictures(posiotion:Int,bean: picture_main)
        fun clanPictures()
        fun stopRefresh()
        fun endLoadMore()
        fun setNetError(error:Boolean)
    }
    interface Presenter{
        fun refreshPictures()
        fun initPictures()
        fun insertPictures(isRefresh:Boolean)
        fun insertPictures()
        fun insertToTop()
    }
}