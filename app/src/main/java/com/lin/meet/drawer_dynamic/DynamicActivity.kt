package com.lin.meet.drawer_dynamic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import com.lin.meet.R
import com.lin.meet.bean.DefaultUtil
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_dynamic.*

@EnableDragToClose
class DynamicActivity : AppCompatActivity(),DynamicContract.View {
    override fun clearData() {
        adapter.clearData()
    }

    override fun endLoadMore() {
        refresh.setLoadMore(false)
    }

    override fun endRefresh() {
        refresh.isRefreshing = false
    }

    override fun insertItem(bean: DynamicBean) {
        adapter.insertContent(bean)
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    private var loadingView: View? = null
    private var refreshView: View? = null
    private val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    private val adapter = DynamicAdapter()
    private val presenter:DynamicContract.Presenter = DynamicPresenter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic)
        initStatusBar()
        initView()
    }

    private fun setLoadingViewStatus(loadingView: View, flag: Int) {
        (loadingView.findViewById(R.id.loading_1) as View).visibility = if (flag == 1) View.VISIBLE else View.GONE
        (loadingView.findViewById(R.id.loading_2) as View).visibility = if (flag == 2) View.VISIBLE else View.GONE
        (loadingView.findViewById(R.id.loading_3) as View).visibility = if (flag == 3) View.VISIBLE else View.GONE
    }

    private fun initStatusBar(){
        window.statusBarColor = resources.getColor(com.lin.meet.R.color.msg_appbar)
        window.decorView.systemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        loadingView = DefaultUtil.createBottomView(this)
        refreshView = DefaultUtil.createTopView(this)
        refresh.setHeaderView(refreshView)
        refresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                if(p0) setLoadingViewStatus(refreshView!!,3)
                else setLoadingViewStatus(refreshView!!,2)
            }

            override fun onPullDistance(p0: Int) {

            }

            override fun onRefresh() {
                setLoadingViewStatus(refreshView!!,1)
                presenter.initNetData()
            }
        })
        refresh.setFooterView(loadingView)
        refresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
            override fun onPushDistance(p0: Int) {

            }

            override fun onPushEnable(p0: Boolean) {
                if(p0) setLoadingViewStatus(loadingView!!,3)
                else setLoadingViewStatus(loadingView!!,2)
            }

            override fun onLoadMore() {
                setLoadingViewStatus(loadingView!!,1)
                presenter.onInsertData()
            }
        })

        presenter.initNetData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
