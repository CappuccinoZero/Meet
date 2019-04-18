package com.lin.meet.personal

import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DownloadFileListener
import com.lin.meet.main.MainActivity.savePath
import java.io.File

class PersonalPresenter(view:PersonalContract.View):PersonalContract.Presenter{

    private var view:PersonalContract.View
    init{
        this.view = view
    }

    override fun downloadToCache(uri: String, fileName: String, result: Int) {
        val file = File(savePath, fileName)
        val bmobFile = BmobFile(fileName, "", uri)
        bmobFile.download(file, object : DownloadFileListener() {
            override fun done(s: String, e: BmobException?) {
                if (e == null) {
                    view.updateImageView(result,s)
                } else {

                }
            }

            override fun onProgress(integer: Int?, l: Long) {

            }
        })
    }
}