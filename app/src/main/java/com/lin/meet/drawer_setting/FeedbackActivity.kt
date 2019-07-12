package com.lin.meet.drawer_setting

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadFileListener
import com.lin.meet.R
import com.lin.meet.db_bean.Feedback
import com.lin.meet.my_util.MyUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.activity_footprint.toolbar
import java.io.File

@EnableDragToClose
class FeedbackActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cancel_upload->{
                closeProgressDialog()
            }
        }
    }

    fun closeProgressDialog() {
        if(progressDialog!=null)
            progressDialog!!.dismiss()
        if(bmobFile!=null){
            bmobFile?.cancel()
            bmobFile = null
        }
    }

    private var uri = ""
    private var bmobFile:BmobFile ?= null
    private var uploadText: TextView? =null
    private var progressView:View? = null
    private var progressDialog:AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_feedback)
        initView()
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar?.setHomeAsUpIndicator(R.mipmap.onback_white)
            supportActionBar?.title = "意见反馈"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        open_photo.setOnClickListener { openPhoto() }
        button.setOnClickListener { senFeedback() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
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

    private fun openPhoto(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isGif(false)
                .isCamera(false)
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(true)
                .compress(true)
                .openClickSound(true)
                .minimumCompressSize(100)
                .isDragFrame(true)
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode==PictureConfig.CHOOSE_REQUEST){
            val media = PictureSelector.obtainMultipleResult(data)[0]
            if(media!=null){
                if(media.isCompressed)
                    uri = media.compressPath
                else if(media.isCut)
                    uri = media.cutPath
                else
                    uri = media.path
            }
        }
    }

    private fun senFeedback(){
        if(content.text.isEmpty()){
            Toast.makeText(this,"请输入您的反馈内容",Toast.LENGTH_SHORT).show()
            return
        }
        if(email.text.isEmpty()){
            Toast.makeText(this,"请告诉我们您的联系方式",Toast.LENGTH_SHORT).show()
            return
        }
        if(!MyUtil.isEmail(email.text.toString())){
            Toast.makeText(this,"请填写正确的邮箱地址",Toast.LENGTH_SHORT).show()
            return
        }
        if(uri.isNotEmpty()){
            createProgressDialog()
            bmobFile = BmobFile(File(uri))
            bmobFile?.uploadblock(object :UploadFileListener(){

                override fun onProgress(value: Int?) {
                    super.onProgress(value)
                }

                override fun done(e: BmobException?) {
                    if(e==null){
                        saveFeedback()
                        uri = bmobFile!!.fileUrl
                    }else{
                        Toast.makeText(this@FeedbackActivity,"上传图片失败,请检查网络",Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }else
            saveFeedback()
    }

    private fun saveFeedback(){
        val bean = Feedback()
        bean.problem = content.text.toString()
        bean.emial = email.text.toString()
        bean.uri = uri
        bean.save(object :SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if(p1==null){
                    Toast.makeText(this@FeedbackActivity,"感谢您的反馈",Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        })
    }
}
