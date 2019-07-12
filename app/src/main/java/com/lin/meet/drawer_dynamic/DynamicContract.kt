package com.lin.meet.drawer_dynamic

interface DynamicContract {
    interface View{
        fun toast(msg:String)
        fun insertItem(bean:DynamicBean)
        fun clearData()
        fun endRefresh()
        fun endLoadMore()
    }

    interface Presenter{
        fun initNetData()
        fun onInsertData()
    }
}