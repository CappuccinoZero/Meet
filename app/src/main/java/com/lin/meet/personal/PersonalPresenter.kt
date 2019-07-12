package com.lin.meet.personal

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.friends

class PersonalPresenter(view:PersonalContract.View):PersonalContract.Presenter{
    override fun checkAttionCount() {
        if(!BmobUser.isLogin())
            return
        checkAttionCount(getUser().uid)
    }

    override fun checkFanCount() {
        if(!BmobUser.isLogin())
            return
        checkFanCount(getUser().uid)
    }

    override fun checkAttionCount(uid: String?) {
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidA",uid)
        query.findObjects(object :FindListener<friends>(){
            override fun done(p0: MutableList<friends>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0){
                    view.setAttend(p0.size.toString())
                }
            }
        })
    }

    override fun checkFanCount(uid: String?) {
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidB",uid)
        query.findObjects(object :FindListener<friends>(){
            override fun done(p0: MutableList<friends>?, p1: BmobException?) {
                if(p1==null&&p0!=null&&p0.size>0){
                    view.setFans(p0.size.toString())
                }
            }
        })
    }

    private var uid:String ?= null

    override fun cancelAttention() {
        onAttention(false)
    }

    override fun attention() {
        onAttention(true)
    }

    private fun onAttention(attion:Boolean){
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidA",getUser().uid)
        query.addWhereEqualTo("uidB",uid)
        query.findObjects(object :FindListener<friends>(){
            override fun done(f: MutableList<friends>?, e: BmobException?) {
                if(e==null&&f!=null&&f.size>0&&!attion){
                    deleteAttention(f[0])
                }else if(e==null&&f!=null&&f.size==0&&attion){
                    saveAttention(getUser().uid,uid!!)
                }else if(e!=null&&!attion)
                    view.attentionResult(success = false, attention = true)
                else if(e!=null&&attion)
                    view.attentionResult(success = false, attention = false)
            }
        })
    }

    private fun saveAttention(uidA:String,uidB:String){
        val friend = friends()
        friend.uidA = uidA
        friend.uidB = uidB
        friend.save(object :SaveListener<String>(){
            override fun done(p0: String?, e: BmobException?) {
                if(e!=null)
                    view.attentionResult(success = false, attention = false)
            }
        })
    }

    private fun deleteAttention(friend: friends){
        friend.delete(object :UpdateListener(){
            override fun done(e: BmobException?) {
                if(e!=null)
                    view.attentionResult(success = false, attention = true)
            }
        })
    }

    private fun getUser():User{
        return BmobUser.getCurrentUser(User::class.java)
    }

    override fun initNetData(uid: String?) {
        this.uid = uid
        view.showAttentionHe()
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(users: MutableList<User>?, e: BmobException?) {
                if(e==null&&users!=null&&users.size>0){
                    initNetUserData(users[0])
                }else if(e==null){
                    view.toast("用户信息获取失败")
                }else{
                    view.toast("网络错误")
                }
            }
        })
        checkAttionCount(uid)
        checkFanCount(uid)
        isAttention(uid!!)
    }

    fun initNetUserData(user:User){
        view.setHeadBg(user.backgroundUri)
        view.setHeader(user.headerUri)
        view.setNumber(user.uid.toString())
        view.setName(user.nickName)
        view.setSex(user.sex)
        view.setWork(user.work)
        view.setBirthday(user.brith)
        view.setEmail(user.e_mail)
        view.setFrom(user.area)
        view.setSignature(user.signature)
        view.setIntroduce(user.introduce)
        view.setAge(view.calculAge(user.brith))
        view.setConstellation(view.calculConstellation(user.brith) )
    }

    fun isAttention(netId:String){
        if(!BmobUser.isLogin())
            return
        if(getUser().uid==uid){
            view.hideAttentionView()
            return
        }
        val query = BmobQuery<friends>()
        query.addWhereEqualTo("uidA",getUser().uid)
        query.addWhereEqualTo("uidB",uid)
        query.findObjects(object :FindListener<friends>(){
            override fun done(f: MutableList<friends>?, e: BmobException?) {
                if(e==null&&f!=null&&f.size>0)
                    view.showAttentioned()
            }
        })
    }

    private var view:PersonalContract.View
    init{
        this.view = view
    }
}