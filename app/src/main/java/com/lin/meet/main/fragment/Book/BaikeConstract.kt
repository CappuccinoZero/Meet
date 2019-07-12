package com.lin.meet.main.fragment.Book

import com.lin.meet.bean.Baike

interface BaikeConstract {
    interface View{
        fun initAdapter()
        fun insertBaike(baike:Baike)
        fun insertBaikeToTop(position:Int,baike:Baike)
        fun endRefresh()
        fun endLoading()
        fun error()
        fun initError()
    }
    interface Presenter{
        fun onInsertBaikeToTop()
        fun onRefreshBaike()
        fun onInsertBaike()
        fun onSearch(msg:String)
    }
}