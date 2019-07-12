package com.lin.meet.know

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadFileListener
import com.lin.meet.R
import com.lin.meet.bean.KnowBean
import com.lin.meet.bean.User
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.PictureFileUtils
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_send_know.*
import java.io.File

@EnableDragToClose
class SendKnowActivity : AppCompatActivity(), View.OnClickListener {
    var path = "@null"
    var file:BmobFile ?= null
    private var progressDialog:AlertDialog? = null
    private var progressView:View? = null
    private var uploadText: TextView? =null

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.open_photo->{
                openPhoto()
            }
            R.id.back->{
                finish()
            }
            R.id.send->{
                send()
            }
            R.id.cancel_upload->{
                cancelLoadUp()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_know)
        initView()
    }

    private fun initView(){
        open_photo.setOnClickListener(this)
        send.setOnClickListener(this)
        back.setOnClickListener(this)
    }

    private fun openPhoto(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .minSelectNum(1)
                .isGif(false)
                .compress(true)
                .minimumCompressSize(150)
                .enableCrop(true)
                .withAspectRatio(3,2)
                .openClickSound(true)
                .scaleEnabled(true)
                .isDragFrame(true)
                .isCamera(false)
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                PictureConfig.CHOOSE_REQUEST->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (selectList[0].isCompressed)
                        path = selectList[0].compressPath
                    else
                        path = selectList[0].path
                }
            }
        }
    }

    private fun send(){
        if(know_edit.text.toString().isNotEmpty()&&BmobUser.isLogin()){
            val bean = KnowBean()
            var id = BmobUser.getCurrentUser(User::class.java).uid+System.currentTimeMillis()
            bean.id = id
            bean.uid = BmobUser.getCurrentUser(User::class.java).uid
            bean.img = path
            bean.solve = false
            bean.content = know_edit.text.toString()
            if(path!="@null"){
                loadUp(bean)
            }
            else{
                saveBean(bean)
            }
        }else{
            Toast.makeText(this,"发送失败",Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUp(bean:KnowBean){
        createProgressDialog()
        val myfile = File(path)
        file = BmobFile(myfile)
        file!!.uploadblock(object :UploadFileListener(){
            override fun done(p0: BmobException?) {
                if(p0==null){
                    bean.img = file!!.fileUrl
                    cancelLoadUp()
                    saveBean(bean)
                }
            }
        })
    }

    private fun saveBean(bean:KnowBean){
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    startKnowActivity(bean)
                }
            }
        })
    }

    fun startKnowActivity(bean:KnowBean){
        var intent:Intent = Intent(this,KnowActivity::class.java)
        intent.putExtra("id",bean.id)
        intent.putExtra("uid",bean.uid)
        intent.putExtra("img",bean.img)
        val data = Intent()
        data.putExtra("ID",bean.id)
        setResult(2001,data)
        startActivity(intent)
        PictureFileUtils.deleteCacheDirFile(this)
        finish()
    }

    fun createProgressDialog() {
        progressView = layoutInflater.inflate(R.layout.progress_view,null)
        uploadText = progressView!!.findViewById(R.id.cancel_upload)
        uploadText!!.setOnClickListener(this)
        var build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setCancelable(false)
        build.setView(progressView)
        progressDialog = build.create()
        progressDialog!!.show()
    }

    fun closeProgressDialog() {
        if(progressDialog!=null)
            progressDialog!!.dismiss()
    }

    fun cancelLoadUp(){
        if(file!=null){
            file!!.cancel()
            file = null
        }
        closeProgressDialog()
    }
}
