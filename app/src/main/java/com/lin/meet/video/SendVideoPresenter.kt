package com.lin.meet.video

import android.util.Log
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadFileListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.video_main
import java.io.File

class SendVideoPresenter(view:VideoContract.SendView):VideoContract.SendPresenter {
    override fun cancelLoad() {
        if(bmobFile!=null)
            bmobFile!!.cancel()
        bmobFile = null
        view.closeProgressDialog()
    }

    override fun onSendVideoFromPath(path: String?,title: String) {
        if(!BmobUser.isLogin()){
            view.toast("登录后才能分享视频")
            return
        }
        view.createProgressDialog()
        bmobFile = BmobFile(File(path))
        bmobFile!!.uploadblock(object :UploadFileListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    view.toast("上传成功")
                    view.closeProgressDialog()
                    saveVideoMain(bmobFile!!.fileUrl,title)
                }
            }
        })
    }

    private fun saveVideoMain(uri: String?,title:String){
        val video = video_main()
        video.id = getVideoId()
        video.uid = getUser().uid
        video.tltle = title
        video.uri = uri
        video.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    view.saveVideoResult(1,video.id)
                }
            }
        })
    }

    override fun onSendVideoFromUri(uri: String?,title: String) {
        Log.d("发送URI","测试000")
        if(!BmobUser.isLogin()){
            view.toast("登录后才能分享视频")
            return
        }
        Log.d("发送URI","测试000")
        saveVideoMain(uri,title)
    }

    private var bmobFile:BmobFile ?= null
    private val view = view

    private fun getUser(): User {
        return BmobUser.getCurrentUser(User::class.java)
    }

    private fun getVideoId():String{
        return getUser().uid+System.currentTimeMillis()
    }

}