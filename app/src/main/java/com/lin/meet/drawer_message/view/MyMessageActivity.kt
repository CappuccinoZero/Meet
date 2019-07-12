package com.lin.meet.drawer_message.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.lin.meet.drawer_message.adapter.MessageAdapter
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_my_message.*


@EnableDragToClose
class MyMessageActivity : AppCompatActivity() {
    var adapter = MessageAdapter(supportFragmentManager)
    val title = arrayOf("消息", "点赞")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.lin.meet.R.layout.activity_my_message)
        initView()
    }

    fun initView(){
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "消息"
        }
        initStatusBar()
        viewPage.adapter = adapter
        viewPage.offscreenPageLimit = 3
        tabLayout.setTitles(*title)
        tabLayout.stripType = NavigationTabStrip.StripType.LINE
        tabLayout.setViewPager(viewPage)
    }

    private fun initStatusBar(){
        window.statusBarColor = resources.getColor(com.lin.meet.R.color.msg_appbar)
        window.decorView.systemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
