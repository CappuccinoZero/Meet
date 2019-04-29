package com.lin.meet.video

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.lin.meet.R
import com.lin.meet.my_util.MyUtil
import com.lin.meet.override.SmoothCheckBox
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_send_video.*

class SendVideo : AppCompatActivity(),VideoContract.SendView,View.OnClickListener{
    override fun saveVideoResult(resultCode: Int, id: String?) {
        if(resultCode == 1){
            val data = Intent()
            data.putExtra("VIDEO",id)
            val intent = Intent(this,VideoActivity::class.java)
            intent.putExtra("VIDEO",id)
            startActivity(intent)
            setResult(12,data)
            finish()
        }
    }

    override fun createProgressDialog() {
        progressView = layoutInflater.inflate(R.layout.progress_view,null)
        uploadText = progressView!!.findViewById(R.id.cancel_upload)
        uploadText!!.setOnClickListener(this)
        var build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setCancelable(false)
        build.setView(progressView)
        progressDialog = build.create()
        progressDialog!!.show()
    }

    override fun closeProgressDialog() {
        if(progressDialog!=null)
            progressDialog!!.dismiss()
    }

    override fun UseUri(isUse: Boolean) {
        video_layout.visibility = if(isUse)View.GONE else View.VISIBLE
        uri_Card.visibility = if(isUse)View.VISIBLE else View.GONE
        uri_layout.visibility = if(isUse)View.VISIBLE else View.GONE
        check_uri.visibility = if(isUse)View.VISIBLE else View.GONE
    }

    override fun checkUri(send: Boolean) {
        if(!send){}
        else if(!readySend()){
            return
        }
        val url = if(box.isChecked)uri_edit.text.toString() else path
        val glide = Glide.with(this).load(url).listener(object :RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                toast("视频获取失败")
                setUriState(false)
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                setUriState(true)
                if(send&&box.isChecked)
                    presenter!!.onSendVideoFromUri(uri,video_title.text.toString())
                else if(send)
                    presenter!!.onSendVideoFromPath(path,video_title.text.toString())
                return false
            }
        }).apply(RequestOptions().skipMemoryCache(true))
        if(box.isChecked)
            glide.into(uri_image)
        else
            glide.into(video_image)

    }

    override fun toast(msg: String?) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun readySend(): Boolean {
        if(box.isChecked&&!MyUtil.checkVideo(uri_edit.text.toString())){
            toast("URI格式错误")
            return false
        }
        if(!box.isChecked&&path=="@null"){
            toast("未添加视频")
            return false
        }
        if(video_title.text.toString().isEmpty()){
            toast("未添加标题")
            return false
        }
        return true
    }

    override fun setVideoState(life: Boolean?) {
        start_video.visibility = if(life!!) View.VISIBLE else View.GONE
        close.visibility = if(life) View.VISIBLE else View.GONE
        add_video.visibility = if(life)View.GONE else View.VISIBLE
        if(!life){
            video_image.setImageResource(R.color.video_default)
            path = "@null"
        }
    }

    override fun showAddVideo(show: Boolean) {
        add_video.visibility = if(show)View.VISIBLE else View.GONE
    }

    override fun showStartVideo(show: Boolean) {
        start_video.visibility = if(show)View.VISIBLE else View.GONE
    }

    private var progressDialog: AlertDialog? = null
    private var progressView:View? = null
    private var uploadText: TextView? =null
    private var presenter:VideoContract.SendPresenter ?= null
    private var path = "@null"
    private var uri = "@null"
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.send->{
                Log.d("发送URI","测试sendclick")
                checkUri(true)
            }
            R.id.back->{
                finish()
            }
            R.id.close->{
                setVideoState(false)
            }
            R.id.video_image->{
                if(path == "@null"){
                    openPhoto()
                }else{
                    playVideo()
                }
            }
            R.id.start_video->{
                playVideo()
            }
            R.id.add_video->{
                openPhoto()
            }
            R.id.uri_close->{
                uri_edit.setText("")
                setUriState(false)
                getUriFocus()
            }
            R.id.uri_image->{
                if(uri != "@null"){
                    playVideo()
                }
            }
            R.id.uri_play->{
                playVideo()
            }
            R.id.uri_add->{
                getUriFocus()
            }
            R.id.check_uri->{
                checkUri(false)
            }
            R.id.cancel_upload->{
                presenter!!.cancelLoad()
            }
        }
    }

    override fun showVideoImage(show: Boolean) {
        video_layout.visibility = if(show)View.VISIBLE else View.GONE
    }

    override fun showUriEdit(show: Boolean) {
        uri_layout.visibility = if(show)View.VISIBLE else View.GONE
    }

    override fun setVideoImage(uri: String?) {
        Glide.with(this).load(uri).into(video_image)
    }

    private fun initView(){
        presenter = SendVideoPresenter(this)
        box.setOnCheckedChangeListener(object :SmoothCheckBox.OnCheckedChangeListener{
            override fun onCheckedChanged(checkBox: SmoothCheckBox?, isChecked: Boolean) {
                UseUri(box.isChecked)
            }
        })
        send.setOnClickListener(this)
        back.setOnClickListener(this)
        close.setOnClickListener(this)
        video_image.setOnClickListener(this)
        start_video.setOnClickListener(this)
        add_video.setOnClickListener(this)

        uri_close.setOnClickListener(this)
        uri_image.setOnClickListener(this)
        uri_play.setOnClickListener(this)
        uri_add.setOnClickListener(this)
        check_uri.setOnClickListener(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_video)
        initView()
        uri_edit.setText("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                PictureConfig.CHOOSE_REQUEST->{
                    val media = PictureSelector.obtainMultipleResult(data)[0]
                    path = media.path
                    setVideoImage(path)
                    setVideoState(true)
                }
            }
        }
    }

    private fun openPhoto(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())
                .previewVideo(true)
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    private fun playVideo(){
        if(box.isChecked)
            PictureSelector.create(this).externalPictureVideo("http://bmob-cdn-24942.b0.upaiyun.com/2019/04/25/aa0bdce7de8d4c43ab1aaef9330cae7a.mp4")
        else
            PictureSelector.create(this).externalPictureVideo("http://bmob-cdn-24942.b0.upaiyun.com/2019/04/25/aa0bdce7de8d4c43ab1aaef9330cae7a.mp4")
    }

    private fun getUriFocus(){
        uri_edit.setSelection(uri_edit.text.toString().length)
        uri_edit.requestFocus()
    }

    private fun setUriState(life: Boolean){
        uri_play.visibility = if(life!!) View.VISIBLE else View.GONE
        uri_close.visibility = if(life) View.VISIBLE else View.GONE
        uri_add.visibility = if(life)View.GONE else View.VISIBLE
        if(!life){
            uri_image.setImageResource(R.color.video_default)
            uri = "@null"
        }else
            uri = uri_edit.text.toString()
    }

}
