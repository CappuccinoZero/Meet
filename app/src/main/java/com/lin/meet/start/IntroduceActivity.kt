@file:Suppress("UNREACHABLE_CODE")

package com.lin.meet.start

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lin.meet.IntroductionPage.IntroductionActivity
import com.lin.meet.IntroductionPage.ViewPageAdapter
import com.lin.meet.R
import com.lin.meet.main.MainActivity
import com.lin.meet.my_util.MyUtil
import kotlinx.android.synthetic.main.activity_friends.viewPage
import kotlinx.android.synthetic.main.activity_introduce.*
import java.util.*
import kotlin.collections.ArrayList


class IntroduceActivity : AppCompatActivity() {
    private var isFirst = false
    private var view1:View ?= null
    private var view2:View ?= null
    private var view3:View ?= null
    private val views = ArrayList<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(com.lin.meet.R.layout.activity_introduce)
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView(){
        isFirst = intent.getBooleanExtra("isFirst",true)
        val list = ArrayList<View>()
        val view_1 = LayoutInflater.from(this).inflate(R.layout.introduece_view_1,null,false)
        val view_2 = LayoutInflater.from(this).inflate(R.layout.introduece_view_1,null,false)
        val view_3 = LayoutInflater.from(this).inflate(R.layout.introduece_view_1,null,false)
        val view_4 = LayoutInflater.from(this).inflate(R.layout.introduece_view_2,null,false)
        initSonView(view_1,R.drawable.bg_4,resources.getString(R.string.introduce_title_1),resources.getString(R.string.introduce_content_1))
        initSonView(view_2,R.drawable.bg_5,resources.getString(R.string.introduce_title_2),resources.getString(R.string.introduce_content_2))
        initSonView(view_3,R.drawable.bg_6,resources.getString(R.string.introduce_title_3),resources.getString(R.string.introduce_content_3))
        initSonLastView(view_4,R.drawable.bg_7,resources.getString(R.string.introduce_title_4),resources.getString(R.string.introduce_content_4))
        list.add(view_1)
        list.add(view_2)
        list.add(view_3)
        list.add(view_4)
        val adapter = ViewPageAdapter(list)
        viewPage.adapter = adapter
        viewPage.setPageTransformer(true, IntroductionActivity.DepthPageTransformer())
        indicator.setViewPager(viewPage)
        if(isFirst)
            back.visibility = View.GONE
        else
            back.setOnClickListener { onBackPressed() }
    }

    private fun initSonView(view:View,id:Int,titleStr:String,contentStr:String){
        val img = view.findViewById<ImageView>(R.id.background)
        val title = view.findViewById<TextView>(R.id.title)
        val content = view.findViewById<TextView>(R.id.content)
        img.setImageResource(id)
        title.text = titleStr
        content.text = contentStr
    }

    private fun initSonLastView(view:View,id:Int,titleStr:String,contentStr:String){
        initSonView(view,id,titleStr,contentStr)
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val map = HashMap<String,Boolean>()
            map["firstIn"] = true
            MyUtil.saveSharedBooleanPreferences(this, "FirstIn", map)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }
        if(!isFirst)
            button.visibility = View.GONE
    }
}
