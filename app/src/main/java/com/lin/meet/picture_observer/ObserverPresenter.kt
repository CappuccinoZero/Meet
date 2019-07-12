package com.lin.meet.picture_observer

import android.content.Intent
import android.net.Uri
import android.os.Environment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DownloadFileListener
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadFileListener
import com.lin.meet.bean.User
import com.lin.meet.db_bean.picture_main
import java.io.File

class ObserverPresenter():ObserverContract.Presenter ,ObserverContract.SendPresenter{
    override fun updateHot(bean: picture_main) {
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
            }
        })
    }

    override fun cancelUpload() {
        if(bmobFile!=null){
            bmobFile!!.cancel()
            bmobFile = null
        }
    }

    override fun SendPicture(title: String, content: String, path: String) {
        if(title.isEmpty()){
            sendView?.toast("主题必须填写")
            return
        }
        if(path.isEmpty()){
            sendView?.toast("必须添加图片")
            return
        }
        if(!BmobUser.isLogin()){
            sendView?.toast("需要登录")
            return
        }
        val user = BmobUser.getCurrentUser(User::class.java)
        sendView?.showLoadingDialog()
        bmobFile = BmobFile(File(path))
        bmobFile?.uploadblock(object :UploadFileListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    val bean = picture_main()
                    bean.uri = bmobFile?.fileUrl
                    bean.uid = user.uid
                    bean.tltle = title
                    bean.content = if(content.isEmpty())"@null" else content
                    updateData(bean)
                }else
                    sendView?.SendResult(0,null)
            }
        })
    }

    private fun updateData(bean: picture_main){
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    sendView?.SendResult(1,bean)
                    sendView?.toast("上传成功")
                    sendView?.closeLoadingDialog()
                }
            }
        })
    }

    var bmobFile:BmobFile ?= null
    var view:ObserverContract.View ?= null
    var sendView:ObserverContract.SendView ?= null
    override fun initAuthorMessage(uid: String) {
        if(uid == "@Meet")
            view?.updateAhthor(null,false)
        val query = BmobQuery<User>()
        query.addWhereEqualTo("uid",uid)
        query.findObjects(object :FindListener<User>(){
            override fun done(list: MutableList<User>?, e: BmobException?) {
                if(e==null&&list!=null&&list.size>0){
                    view?.setHeader(list[0].headerUri)
                    view?.setNickName(list[0].nickName)
                }
            }
        })
    }
    constructor(view:ObserverContract.View):this(){
        this.view = view
    }
    constructor(view:ObserverContract.SendView):this(){
        this.sendView = view
    }

    override fun downloadPicture(uri:String) {
        view?.setDownloadClickable(false)
        val bmobFile = BmobFile(getPictureName(uri),"",uri)
        val file = File(Environment.getExternalStorageDirectory(),bmobFile.filename)
        bmobFile.download(file,object :DownloadFileListener(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    view?.toast("下载成功,保存路径:$p0")
                    updateSystemPictues(p0!!)
                }
                else
                    view?.toast("下载失败")
                view?.setDownloadClickable(true)
            }

            override fun onProgress(p0: Int?, p1: Long) {

            }
        })
    }

    private fun getPictureName(uri:String):String{
        val build = StringBuilder()
        var flag = 0
        for(index in uri.length-1 .. 0){
            if(uri[index]=='/')
                break
            flag = index
        }
        for (index in flag until uri.length){
            build.append(uri[index])
        }
        return build.toString()
    }

    fun updateSystemPictues(path:String){
        val file = File(path)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(file)
        intent.data = uri
        view?.updateBroadcast(intent)
    }
}