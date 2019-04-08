package com.lin.meet.main

interface MainConstract{
    interface View{
        fun setHeader(str:String)
        fun setName(str:String)
        fun getName(str:String):String
    }

    interface Presenter{
        fun loadHeader()
    }

    interface Model{
        fun getName():String
        fun getHeader()
    }
}