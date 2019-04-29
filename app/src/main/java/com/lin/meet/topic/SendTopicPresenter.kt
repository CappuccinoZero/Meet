package com.lin.meet.topic

import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DeleteBatchListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadBatchListener
import com.lin.meet.bean.User
import com.lin.meet.bean.topic_main

class SendTopicPresenter(view:SendTopicConstract.View):SendTopicConstract.Presenter {
    override fun canelUpload() {
        error = true
        var pathStrs = arrayOfNulls<String>(loadFile.size)
        for (index in 0 until loadFile.size)
            pathStrs[index] = loadFile[index]
        BmobFile.deleteBatch(pathStrs,object :DeleteBatchListener(){
            override fun done(p0: Array<out String>?, p1: BmobException?) {
                view.sendResult(0,"@null")
            }
        })

    }

    override fun sendTopic(title:String,content:String,type: String,location:String) {
        if(!checkLogin()){
            view.toast("用户未登录")
            return
        }
        var topicMain = topic_main()
        topicMain.title = title
        topicMain.content = content
        topicMain.uid = getUserUid()
        topicMain.id = getSendId()
        topicMain.type = type
        topicMain.location = location
        uploadFile(topicMain)
    }


    private fun uploadFile(topic:topic_main){
        var pathStrs = arrayOfNulls<String>(paths.size)
        for (index in 0 until paths.size)
            pathStrs[index] = paths[index]
        if(paths.size>0){
            view.createProgressDialog()
            BmobFile.uploadBatch(pathStrs,object : UploadBatchListener {
                override fun onSuccess(p0: MutableList<BmobFile>?, p1: MutableList<String>?) {
                    if(p1!!.size == pathStrs.size){
                        for (index in 0 until p1.size){
                            when(index){
                                0->{
                                    topic.one_uri = p1[index]
                                }
                                1->{
                                    topic.two_uri = p1[index]
                                }
                                2->{
                                    topic.three_uri = p1[index]
                                }
                                3->{
                                    topic.four_uri = p1[index]
                                }
                                4->{
                                    topic.five_uri = p1[index]
                                }
                                5->{
                                    topic.six_uri = p1[index]
                                }
                            }
                        }
                        if(!error){
                            saveTopic(topic)
                            return
                        }
                    }
                    for(index in 0 until p1.size){
                        if(loadFile.contains(p1[index])){
                            loadFile.add(p1[index])
                        }
                    }
                }

                override fun onProgress(p0: Int, p1: Int, p2: Int, p3: Int) {
                }

                override fun onError(p0: Int, p1: String?) {

                }
            })
        }
        else{
            saveTopic(topic)
        }
    }




    private fun saveTopic(topic:topic_main){
        topic.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1 == null){
                    view.sendResult(1,topic.id)
                }else
                    view.sendResult(0,topic.id)
            }
        })
    }

    override fun notifiDataChange() {
        for(index in 0 until 6){
            if(index<paths.size)
                view.showPicture(index,paths[index])
            else
                view.hidePicture(index)
        }
    }

    override fun closePicture(index: Int) {
        if(paths.size>0)
            paths.remove(paths[index])
        notifiDataChange()
    }

    override fun insertPicture(path: String) {
        paths.add(path)
        view.showPicture(paths.size-1,path)
    }

    override fun checkEdit(title: String, content: String) {
        if(title.isNotEmpty() && content.isNotEmpty())
            view.setSendClickable(true)
        else
            view.setSendClickable(false)
    }

    private fun getSendId():String{
        return BmobUser.getCurrentUser(User::class.java).uid+System.currentTimeMillis()
    }

    private fun checkLogin():Boolean{
        if(BmobUser.isLogin())
            return true
        return false
    }

    private fun getUserUid():String{
        return BmobUser.getCurrentUser(User::class.java).uid
    }

    private var loadFile = ArrayList<String>()
    private val view:SendTopicConstract.View = view
    private var paths = ArrayList<String>()
    private var error = false
}