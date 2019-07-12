package com.lin.meet.drawer_footprint

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.MapFlag
import com.lin.meet.bean.User

class FootprintPresenter(view:FootConstract.View):FootConstract.Presenter {
    override fun insertData() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        loadDataFromNet(limit)
    }

    override fun initData() {
        if(!BmobUser.isLogin()){
            view.toast("未登录")
            return
        }
        loadDataFromNet(initCount)
    }

    private fun loadDataFromNet(limit:Int){
        if(loading)
            return
        loading = true
        val query = BmobQuery<MapFlag>()
        query.addWhereEqualTo("uid",getUser().uid)
        query.setLimit(limit)
        query.setSkip(skip)
        query.findObjects(object :FindListener<MapFlag>(){
            override fun done(list: MutableList<MapFlag>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    for(index in 0 until list.size)
                        view.insertFlag(list[index])
                    skip+=list.size
                    loading = false
                }
            }
        })
    }

    private fun getUser():User{
        return User.getCurrentUser(User::class.java)
    }

    var loading = false
    val view = view
    val initCount = 20
    val limit = 10
    var skip = 0

}