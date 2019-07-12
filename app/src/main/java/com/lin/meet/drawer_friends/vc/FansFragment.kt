package com.lin.meet.drawer_friends.vc

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.R
import com.lin.meet.bean.User
import com.lin.meet.db_bean.friends
import com.lin.meet.drawer_friends.adapter.AttentionAdapter
import com.lin.meet.main.fragment.Home.HomeBaseFragment
import kotlinx.android.synthetic.main.message_reply.*

class FansFragment: HomeBaseFragment() {
    private var mView:View ?= null
    private val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
    private val adapter = AttentionAdapter()
    override fun loadData() {
        initView()
        initData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.recycler_item,container,false)
        return mView
    }

    private fun initView(){
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
    }

    private fun initData(){
        if(!BmobUser.isLogin()){
            Toast.makeText(activity,"未登录", Toast.LENGTH_SHORT).show()
            return
        }
        val user = BmobUser.getCurrentUser(User::class.java)
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidB",user.uid)
        query.findObjects(object : FindListener<friends>(){
            override fun done(list: MutableList<friends>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    for(index in 0 until list.size)
                        findUser(list[index].uidA)
                }
            }
        })
    }

    private fun findUser(uid:String){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",uid)
        query.findObjects(object : FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    adapter.insertFriends(list[0])
                }
            }
        })
    }
}