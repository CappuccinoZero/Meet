package com.lin.meet.drawer_setting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.lin.meet.R
import com.lin.meet.start.IntroduceActivity
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_about_app.*

@EnableDragToClose
class AboutApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_about_app)
        initView()
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
            supportActionBar?.setHomeAsUpIndicator(R.mipmap.onback_white)
        }
        version.setOnClickListener { Toast.makeText(this, "已是最新版本",Toast.LENGTH_SHORT).show() }
        appIntroduce.setOnClickListener { appIntroduce() }
        feedback.setOnClickListener { feedback()  }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun appIntroduce(){
        val intent = Intent(this,IntroduceActivity::class.java)
        intent.putExtra("isFirst",false)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }

    private fun feedback(){
        startActivity(Intent(this,FeedbackActivity::class.java))
    }
}
