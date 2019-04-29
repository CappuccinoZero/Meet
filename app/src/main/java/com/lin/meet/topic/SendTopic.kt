package com.lin.meet.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.airsaid.pickerviewlibrary.OptionsPickerView
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lin.meet.R
import com.lin.meet.override.EmojiAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_send_topic.*

class SendTopic : AppCompatActivity(), View.OnClickListener,MyLocationListener.locationCallback,SendTopicConstract.View, TextWatcher,EmojiAdapter.EmojiCallback {


    override fun sendResult(ResultCode: Int,id:String) {
        when(ResultCode){
            1->{
                closeProgressDialog()
                toast("发布成功")
                val intent = Intent(this,TopicActivity::class.java)
                val data = Intent()
                intent.putExtra("ID",id)
                data.putExtra("ID",id)
                setResult(11,data)
                startActivityForResult(intent,0)
                finish()
            }
            0->{
                closeProgressDialog()
                toast("发布失败")
            }
        }
    }

    override fun showPicture(index: Int,path:String) {
        when(index){
            0->{
                loadImage(path,picture_1)
                pictureLayout1.visibility = View.VISIBLE
            }
            1->{
                loadImage(path,picture_2)
                pictureLayout2.visibility = View.VISIBLE
            }
            2->{
                loadImage(path,picture_3)
                pictureLayout3.visibility = View.VISIBLE
            }
            3->{
                loadImage(path,picture_4)
                pictureLayout4.visibility = View.VISIBLE
            }
            4->{
                loadImage(path,picture_5)
                pictureLayout5.visibility = View.VISIBLE
            }
            5->{
                loadImage(path,picture_6)
                pictureLayout6.visibility = View.VISIBLE
            }
        }
    }



    override fun hidePicture(index:Int) {
        when(index){
            0->{
                pictureLayout1.visibility = View.INVISIBLE
            }
            1->{
                pictureLayout2.visibility = View.INVISIBLE
            }
            2->{
                pictureLayout3.visibility = View.INVISIBLE
            }
            3->{
                pictureLayout4.visibility = View.INVISIBLE
            }
            4->{
                pictureLayout5.visibility = View.INVISIBLE
            }
            5->{
                pictureLayout6.visibility = View.INVISIBLE
            }
        }
    }

    override fun hideEmoji() {
        isUserEmoji = !isUserEmoji
        emoji_View.visibility = View.GONE
    }

    override fun showEmoji() {
        isUserEmoji = !isUserEmoji
        emoji_View.visibility = View.VISIBLE
    }

    override fun getEmoji(emoji: String?) {
        val index = topic_content.getSelectionStart()
        val editable = topic_content.getText()
        editable.insert(index, emoji)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        presenter!!.checkEdit(topic_title.text.toString(),topic_content.text.toString())
    }

    override fun setSendClickable(isClick: Boolean) {
        if(isClick){
            send.isClickable = true
            send.setTextColor(resources.getColor(R.color.onclick))
        }
        else{
            send.isClickable = false
            send.setTextColor(resources.getColor(R.color.text_back_0))
        }
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun showLocation(isShow: Boolean) {
        location_view.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun setType(type: String) {
        type_text.text = type
    }

    override fun stop() {
        location_text.text = myListener.province+myListener.city+myListener.district
        Log.d("回复测试","地址信息："+location_text.text.toString())
        isUserLocation = 0
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


    private var progressDialog:AlertDialog? = null
    private var uploadText: TextView? =null
    private var progressView:View? = null
    private var emojiAdapter:EmojiAdapter ?= null
    private var presenter:SendTopicConstract.Presenter ?= null
    private var locationClient: LocationClient ?= null
    private val typeStr = arrayOf("随笔","心情","文章","见闻","遇见")
    private val types = ArrayList<String>()
    private val myListener:MyLocationListener = MyLocationListener(this)
    private var isUserLocation = -1
    private var isUserEmoji = false
    private var focus = 0
    private var glideOption:RequestOptions ?= null

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.location->{
                if(isUserLocation == -1){
                    toast("获取位置失败")
                }else{
                    isUserLocation = 1
                    showLocation(true)
                }
            }
            R.id.cancel_location->{
                isUserLocation = 0
                showLocation(false)
            }
            R.id.type->{
                showPicker()
            }
            R.id.back->{
                finish()
            }
            R.id.send->{
                Log.d("上传进度","onclick")
                presenter!!.sendTopic(topic_title.text.toString(),topic_content.text.toString(),type_text.text.toString(),if(isUserLocation==1) location_text.text.toString() else "@null")
            }
            R.id.emoji->{
                if(!isUserEmoji){
                    showEmoji()
                    closeSoftInput()
                }else{
                    showSoftInput()
                    hideEmoji()
                }
            }
            R.id.picture->{
                openPhoto()
            }
            R.id.close1->{
                presenter!!.closePicture(0)
            }
            R.id.close2->{
                presenter!!.closePicture(1)
            }
            R.id.close3->{
                presenter!!.closePicture(2)
            }
            R.id.close4->{
                presenter!!.closePicture(3)
            }
            R.id.close5->{
                presenter!!.closePicture(4)
            }
            R.id.close6->{
                presenter!!.closePicture(5)
            }
            R.id.cancel_upload->{
                closeProgressDialog()
                presenter!!.canelUpload()
            }
        }
    }

    private fun showSoftInput(){
        if(focus == 2){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(topic_title, 0)
        }
    }

    private fun closeSoftInput(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_topic)
        initTypes()
        init()
    }

    private fun init(){
        presenter = SendTopicPresenter(this)
        type.setOnClickListener(this)
        location.setOnClickListener(this)
        cancel_location.setOnClickListener(this)
        back.setOnClickListener(this)
        send.setOnClickListener(this)
        emoji.setOnClickListener(this)
        picture.setOnClickListener(this)
        close1.setOnClickListener(this)
        close2.setOnClickListener(this)
        close3.setOnClickListener(this)
        close4.setOnClickListener(this)
        close5.setOnClickListener(this)
        close6.setOnClickListener(this)
        topic_title.addTextChangedListener(this)
        topic_content.addTextChangedListener(this)
        topic_content.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                emoji.isClickable = true
                focus = 2
            }
            else{
                focus = 1
                emoji.isClickable = false
                if(isUserEmoji)
                    hideEmoji()
            }
        }

        val option:LocationClientOption = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setIsNeedAddress(true)
        locationClient = LocationClient(this)
        locationClient?.registerLocationListener(myListener)
        locationClient?.locOption = option
        locationClient?.start()

        emojiAdapter = EmojiAdapter(this)
        emoji_View.adapter = emojiAdapter

        glideOption = RequestOptions()
    }


    private fun initTypes(){
        for(index in 0 until typeStr.size)
            types.add(typeStr[index])
    }

    private fun showPicker(){
        val picker:OptionsPickerView<String> = OptionsPickerView(this)
        picker.setPicker(types)
        picker.setOnOptionsSelectListener(object :OptionsPickerView.OnOptionsSelectListener{
            override fun onOptionsSelect(option1: Int, option2: Int, option3: Int) {
                setType(types[option1])
            }
        })
        picker.show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if(ev!!.y<bottom_util.top){
            if(isUserEmoji){
                hideEmoji()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun openPhoto(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(6)
                .minSelectNum(1)
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    private fun openPhoto(index: Int){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .minSelectNum(1)
                .forResult(index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                PictureConfig.CHOOSE_REQUEST->{
                    var selectList = PictureSelector.obtainMultipleResult(data)
                    for(index in 0 until selectList.size){
                        presenter!!.insertPicture(selectList[index].path)
                    }
                }
                in 0 until 6->{
                    var selectList = PictureSelector.obtainMultipleResult(data)
                    presenter!!.insertPicture(selectList[requestCode].path)
                }
            }
        }
    }

    private fun loadImage(path: String,view:ImageView){
            Glide.with(this).load(path).apply(glideOption!!).into(view)
    }
}
