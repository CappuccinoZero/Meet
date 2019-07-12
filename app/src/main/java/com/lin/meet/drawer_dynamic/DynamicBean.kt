package com.lin.meet.drawer_dynamic

class DynamicBean {
    var header:String = ""
    var nickName:String = ""
    var title:String = ""
    var time:String = ""
    var img:String = ""
    var flag:Int = 0
    var uid:String = ""
    var id:String = ""
    var pContent:String = ""
    var timeArr = 0L
    fun setArr(time:String){
        val build = StringBuilder()
        for(index in 0 until time.length)
            if(time[index] in '0'..'9')
                build.append(time[index])
        timeArr = build.toString().toLong()
    }
}