package com.lin.meet.picture_observer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.lin.meet.R
import com.lin.meet.db_bean.picture_main
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.PictureFileUtils
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_send_piture.*
@EnableDragToClose
class SendPitureActivity : AppCompatActivity(),ObserverContract.SendView {
    private var progressView: View? = null
    private var uploadText: TextView? =null
    private var progressDialog:AlertDialog? = null
    override fun showLoadingDialog() {
        progressView = layoutInflater.inflate(R.layout.progress_view,null)
        uploadText = progressView!!.findViewById(R.id.cancel_upload)
        var build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setCancelable(false)
        build.setView(progressView)
        progressDialog = build.create()
        progressDialog!!.show()
        uploadText!!.setOnClickListener{
            closeLoadingDialog()
            presenter.cancelUpload()
        }
    }

    override fun closeLoadingDialog() {
        if(progressDialog!=null)
            progressDialog!!.dismiss()
    }

    override fun SendResult(code: Int, bean: picture_main?) {
        if(code == 1){
            val intent = Intent(this,PictureObserver::class.java)
            intent.putExtra("haveContent",true)
            intent.putExtra("bean",bean)
            startActivity(intent)
            finish()
        }else{
            toast("上传失败,请稍后重试")
        }
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    val presenter:ObserverContract.SendPresenter = ObserverPresenter(this)
    val QUEST_CODE = 10000
    var path = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_piture)
        initView()
        openPhoto(QUEST_CODE)
    }

    fun initView(){
        titleEdit.requestFocus()
        send.setOnClickListener {
            presenter.SendPicture(titleEdit.text.toString(),contentEdit.text.toString(),path)
        }
        closePicture.setOnClickListener { openPhoto(QUEST_CODE) }
        back.setOnClickListener { onBackPressed() }
    }

    fun loadImg(){
        Glide.with(this).load(path).into(img)
    }

    fun openPhoto(code:Int){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isGif(false)
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(true)
                .compress(true)
                .openClickSound(true)
                .minimumCompressSize(500)
                .isCamera(false)
                .isDragFrame(true)
                .forResult(code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode==QUEST_CODE){
            val media = PictureSelector.obtainMultipleResult(data)[0]
            if(media.isCompressed)
                path = media.compressPath
            else
                path = media.path
            loadImg()
        }
        else if(requestCode==QUEST_CODE){
            finish()
        }
    }

    override fun onDestroy() {
        PictureFileUtils.deleteCacheDirFile(this)
        super.onDestroy()
    }
}
