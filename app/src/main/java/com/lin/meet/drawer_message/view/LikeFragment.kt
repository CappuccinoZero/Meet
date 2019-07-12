package com.lin.meet.drawer_message.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import com.lin.meet.R
import com.lin.meet.bean.DefaultUtil
import com.lin.meet.drawer_message.Bean.Like
import com.lin.meet.drawer_message.MessageConstract
import com.lin.meet.drawer_message.adapter.LikeAdapter
import com.lin.meet.drawer_message.presenter.LikePresenter
import com.lin.meet.main.fragment.Home.HomeBaseFragment
import kotlinx.android.synthetic.main.message_reply.*

class LikeFragment: HomeBaseFragment(),MessageConstract.LikeView {
    override fun endLoadMore() {
        refresh.setLoadMore(false)
    }
    private var loadingView: View? = null
    private var refreshView: View? = null
    private var netError: View? = null
    private val presenter:MessageConstract.LikePresenter = LikePresenter(this)
    override fun toast(msg: String) {
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show()
    }

    override fun stopRefresh() {
        refresh.isRefreshing = false
    }

    override fun refreshLike() {
        adapter.refreshReply()
    }

    override fun insertLike(like: Like) {
        adapter.insertReply(like)
    }

    var mView:View ?= null
    val adapter = LikeAdapter()
    val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = layoutInflater.inflate(R.layout.message_reply, container, false)
        return mView
    }

    private fun setLoadingViewStatus(loadingView: View, flag: Int) {
        (loadingView.findViewById(R.id.loading_1) as View).visibility = if (flag == 1) View.VISIBLE else View.GONE
        (loadingView.findViewById(R.id.loading_2) as View).visibility = if (flag == 2) View.VISIBLE else View.GONE
        (loadingView.findViewById(R.id.loading_3) as View).visibility = if (flag == 3) View.VISIBLE else View.GONE
    }

    override fun loadData() {
        presenter.refreshData()
        loadingView = DefaultUtil.createBottomView(context)
        refreshView = DefaultUtil.createTopView(context)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        refresh.setHeaderView(refreshView)
        refresh.setOnPullRefreshListener(object :SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                if(p0) setLoadingViewStatus(refreshView!!,3)
                else setLoadingViewStatus(refreshView!!,2)
            }

            override fun onPullDistance(p0: Int) {

            }

            override fun onRefresh() {
                setLoadingViewStatus(refreshView!!,1)
                presenter.refreshData()
            }
        })
        refresh.setFooterView(loadingView)
        refresh.setOnPushLoadMoreListener(object :SuperSwipeRefreshLayout.OnPushLoadMoreListener{
            override fun onPushDistance(p0: Int) {

            }

            override fun onPushEnable(p0: Boolean) {
                if(p0) setLoadingViewStatus(loadingView!!,3)
                else setLoadingViewStatus(loadingView!!,2)
            }

            override fun onLoadMore() {
                setLoadingViewStatus(loadingView!!,1)
                presenter.insertData()
            }
        })
    }
}