package com.lin.meet.drawer_collection

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.lin.meet.R
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.activity_dynamic.toolbar

@EnableDragToClose
class CollectionActivity : AppCompatActivity() ,CollContract.View{
    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun insertItem(bean: CollBean) {
        adapter.insertContent(bean)
    }

    private val presenter:CollContract.Presenter = CollPresenter(this)
    private val adapter = CollAdapter()
    private val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        initView()
    }

    private fun initView(){
        initStatusBar()
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        presenter.initNetData()
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
