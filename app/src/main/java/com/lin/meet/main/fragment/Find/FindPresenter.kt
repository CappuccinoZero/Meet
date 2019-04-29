package com.lin.meet.main.fragment.Find

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadFileListener
import com.lin.meet.bean.MapFlag
import com.lin.meet.bean.User
import java.io.File

class FindPresenter(view:FindContract.View):FindContract.Presenter {
    override fun refreshMark() {
        val query = BmobQuery<MapFlag>()
        query.findObjects(object :FindListener<MapFlag>(){
            override fun done(p0: MutableList<MapFlag>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    for(index in 0 until p0.size)
                        selectUser(p0[index])
                }
            }
        })
    }

    fun selectUser(flag: MapFlag){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",flag.uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1==null&&p0!!.size>0){
                    view.insertFlag(flag,p0[0])
                }
            }
        })
    }

    override fun cancelUp() {
        if(bmobFile!=null){
            bmobFile!!.cancel()
            bmobFile = null
        }
    }

    var bmobFile:BmobFile ?= null
    override fun onSend(msg: String, image: String,x:Double,y:Double) {
        if(!BmobUser.isLogin())
            return
        view.createProgressDialog()
        bmobFile = BmobFile(File(image))
        bmobFile!!.uploadblock(object : UploadFileListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    view.toast("上传成功")
                    view.closeProgressDialog()
                    onSaveSend(bmobFile!!.fileUrl,msg,x,y)
                }
            }
        })
    }

    private fun onSaveSend(uri:String,msg:String,x:Double,y:Double){
        var bean = MapFlag()
        bean.x = x
        bean.y = y
        bean.content = msg
        bean.image = uri
        bean.uid = getUser().uid
        bean.id = getId()
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.SendResult(true)
                    view.insertFlag(bean,getUser())
                }
            }
        })
    }

    private fun getUser():User{
        return BmobUser.getCurrentUser(User::class.java)
    }

    private fun getId():String{
        return getUser().uid+System.currentTimeMillis()
    }


    private val view = view
}