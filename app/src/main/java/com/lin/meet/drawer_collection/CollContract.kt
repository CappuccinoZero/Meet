package com.lin.meet.drawer_collection

interface CollContract {
    interface View{
        fun toast(msg:String)
        fun insertItem(bean:CollBean)
    }

    interface Presenter{
        fun initNetData()
    }
}