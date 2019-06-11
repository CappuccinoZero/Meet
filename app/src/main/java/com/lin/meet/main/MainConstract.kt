package com.lin.meet.main

interface MainConstract{
    interface View{
        fun setHeader(str:String)
        fun setHeaderBackground(str:String)
        fun setName(str:String)
        fun getName(str:String):String
        fun updateImageView(id:Int,path:String)
    }

    interface Presenter{
        fun downloadToCache(uri:String,fileName:String,result:Int)
    }

    interface MainDrawerCallback {
        fun openDrawer()
        fun closeDrawer()
    }

    interface Model{
        fun getName():String
        fun getHeader()
    }
}