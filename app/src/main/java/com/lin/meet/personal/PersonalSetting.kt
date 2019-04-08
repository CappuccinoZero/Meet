package com.lin.meet.personal

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_persetting.*

class PersonalSetting : AppCompatActivity(), View.OnClickListener,PerStContract.View {
    private var checkEdit:Int = 0 //ck 1-6
    private var isSaveEdit:Boolean = true
    override fun setHeader(url: String) {
        Glide.with(this).asDrawable().load(url).into(perStHead)
    }

    override fun setBackground(url: String) {
        Glide.with(this).asDrawable().load(url).into(perStBackground)
    }

    override fun setName(str: String) {
        perStNameText.text = str
    }

    override fun getName(): String {
        return perStNameText.text.toString()
    }

    override fun setSex(str: String) {
        perStSexText.text = str
    }

    override fun getSex(): String {
        return perStSexText.text.toString()
    }

    override fun setWork(str: String) {
        perStWorkText.text = str
    }

    override fun getWork(): String {
        return perStWorkText.text.toString()
    }

    override fun setEmail(str: String) {
        perStEmailText.text = str
    }

    override fun getEmail(): String {
        return perStEmailText.text.toString()
    }

    override fun setConstellation(str: String) {
        perStConstellationText.text = str
    }

    override fun getConstellation(): String {
        return perStConstellationText.text.toString()
    }

    override fun setFrom(str: String) {
        perStFromText.text = str
    }

    override fun getFrom(): String {
        return perStFromText.text.toString()
    }

    private var isSetting = false
    override fun showLongEdit(msg: String, id: Int) {
        isSetting = true
        stNormalLayout.visibility = View.GONE
        stLongEdit.setText("")
        stLongEdit.visibility = View.VISIBLE
        stLongEdit.hint = msg
        perStSave.visibility = View.VISIBLE
    }

    override fun showShortEdit(msg: String, id: Int) {
        isSetting = true
        stNormalLayout.visibility = View.GONE
        stShortEdit.visibility = View.VISIBLE
        stShortEdit.setText("")
        stShortEdit.hint = msg
        perStSave.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.perStHeader->{

            }
            R.id.perStBg->{

            }
            R.id.perStName->{
                showShortEdit("请输入您的昵称",0)
            }
            R.id.perStSex->{

            }
            R.id.perStWork->{
                showShortEdit("请输入您的职业",0)
            }
            R.id.perStEmail->{
                showShortEdit("请输入您的邮箱",0)
            }
            R.id.perStXz->{

            }
            R.id.perStFrom->{

            }
            R.id.perStSignature->{
                showLongEdit("请输入您的个性签名",0)
            }
            R.id.perStIntroduce->{
                showLongEdit("请输入您的自我介绍",0)
            }
            R.id.perStSave->{
                isSaveEdit = true
            }
            R.id.stLongEdit,R.id.stShortEdit->{
                isSaveEdit = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor=resources.getColor(R.color.camera_setting_color)
        setContentView(R.layout.activity_persetting)
        initView()
    }

    private fun initView(){
        setSupportActionBar(perStToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.back_x)
        perStBg.setOnClickListener(this)
        perStEmail.setOnClickListener(this)
        perStFrom.setOnClickListener(this)
        perStHeader.setOnClickListener(this)
        perStXz.setOnClickListener(this)
        perStIntroduce.setOnClickListener(this)
        perStName.setOnClickListener(this)
        perStSignature.setOnClickListener(this)
        perStSex.setOnClickListener(this)
        perStWork.setOnClickListener(this)
        perStSave.setOnClickListener(this)
        stLongEdit.setOnClickListener(this)
        stShortEdit.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home) {
            if(isSetting){
                saveEditFinish()
            }else
                finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveEditFinish(){
        if(isSaveEdit){
            finishSave()
        }else{
            AlertDialog.Builder(this)
                    .setMessage("确定保存更改？")
                    .setTitle("未保存")
                    .setPositiveButton("确定",object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            runOnUiThread(Runnable {
                                finishSave()
                            })
                        }
                    })
                    .setNeutralButton("取消",object :DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            runOnUiThread(Runnable {
                                finishSave()
                            })
                        }
                    })
                    .create().show()
        }
    }

    private fun finishSave(){
        isSetting = false
        isSaveEdit = true
        stNormalLayout.visibility = View.VISIBLE
        perStSave.visibility = View.GONE
        stLongEdit.visibility = View.GONE
        stShortEdit.visibility = View.GONE
    }
}
