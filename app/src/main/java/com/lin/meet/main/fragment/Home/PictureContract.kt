package com.lin.meet.main.fragment.Home

import com.lin.meet.bean.video_main

interface PictureContract {
    interface View{
        fun insertPictures(bean: video_main)
        fun insertPictures(posiotion:Int,bean: video_main)
        fun clanPictures()
        fun stopRefresh()
    }
    interface Presenter{
        fun refreshPictures()
        fun initPictures()
        fun insertPictures(isRefresh:Boolean)
        fun insertPictures()
        fun insertToTop()
    }
}