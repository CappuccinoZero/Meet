package com.lin.meet.drawer_friends.vc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.lin.meet.R
import com.lin.meet.drawer_friends.adapter.FriendsAdapter
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_friends.*

@EnableDragToClose
class FriendsActivity : AppCompatActivity() {
    val adapter:FriendsAdapter = FriendsAdapter(supportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        initView()
    }

    private fun initView(){
        initStatusBar()
        tabLayout.addTab(tabLayout.newTab().setText("关注"))
        tabLayout.addTab(tabLayout.newTab().setText("粉丝"))
        viewPage.adapter = adapter
        tabLayout.setupWithViewPager(viewPage)
        search.setOnClickListener { onSearch() }
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "好友"
        }
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

    private fun onSearch(){

    }
}
