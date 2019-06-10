package com.lin.meet.main.fragment.Know

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User

class KnowPresenter(view:KnowConstarct.View):KnowConstarct.Presenter {
    override fun onInsertKnowToTop() {
        val query:BmobQuery<KnowBean> = BmobQuery()
        query.setLimit(page)
        query.setSkip(paged)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p1 == null){
                    paged += p0!!.size
                    for(index in 0 until p0.size){
                        selectUser(index,p0[index])
                    }
                    view.endRefresh()
                }
            }
        })
    }

    override fun insertKnow(id: String) {
        val query:BmobQuery<KnowBean> = BmobQuery()
        query.addWhereEqualTo("id",id)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p1 == null){
                    selectUser(0,p0!![0])
                    view.endRefresh()
                }
            }
        })
    }

    var paged = 0
    var page = 10
    var loading = false

    override fun refreshKnows() {
        paged = 0
        view.refreshAdapter()
        insetKnow()
    }

    override fun insetKnow() {
        if(loading)return
        loading = true
        val query:BmobQuery<KnowBean> = BmobQuery()
        query.setLimit(page)
        query.setSkip(paged)
        query.order("-updatedAt")
        query.findObjects(object :FindListener<KnowBean>(){
            override fun done(p0: MutableList<KnowBean>?, p1: BmobException?) {
                if(p1 == null){
                    paged += p0!!.size
                    for(index in 0 until p0.size){
                        selectUser(p0[index])
                    }
                    view.endRefresh()
                }
                loading = false
            }
        })
    }

    private fun selectUser(bean:KnowBean){
        val query:BmobQuery<User> = BmobQuery()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null){
                    val temp = KnowAndUser()
                    temp.bean = bean
                    temp.user = p0!![0]
                    view.insertKnow(temp)
                }
            }
        })
    }

    private fun selectUser(position:Int,bean:KnowBean){
        val query:BmobQuery<User> = BmobQuery()
        query.addWhereEqualTo("uid",bean.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null){
                    val temp = KnowAndUser()
                    temp.bean = bean
                    temp.user = p0!![0]
                    view.insertKnow(position,temp)
                }
            }
        })
    }


    private val view = view
}