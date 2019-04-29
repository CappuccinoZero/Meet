package com.lin.meet.main.fragment.Book

import com.lin.meet.bean.Baike

interface BaikeConstract {
    interface View{
        fun initAdapter()
        fun insertBaike(baike:Baike)
        fun endRefresh()
    }
    interface Presenter{
        fun onRefreshBaike()
        fun onInsertBaike()
        fun onSearch(msg:String)
    }
}