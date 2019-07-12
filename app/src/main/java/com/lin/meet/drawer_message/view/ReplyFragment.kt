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
import com.lin.meet.drawer_message.Bean.Reply
import com.lin.meet.drawer_message.MessageConstract
import com.lin.meet.drawer_message.adapter.ReplyAdapter
import com.lin.meet.drawer_message.presenter.Presenter
import com.lin.meet.main.fragment.Home.HomeBaseFragment
import kotlinx.android.synthetic.main.message_reply.*

class ReplyFragment: HomeBaseFragment() , MessageConstract.ReplyView{
    override fun endLoadMore() {
        refresh.setLoadMore(false)
    }

    private var loadingView: View? = null
    private var refreshView: View? = null
    override fun stopRefresh() {
        refresh.isRefreshing = false
    }

    override fun toast(msg: String) {
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show()
    }

    override fun refreshReply() {
        adapter.refreshReply()
    }

    override fun insertReply(reply: Reply) {
        adapter.insertReply(reply)
    }

    var mView:View ?= null
    val adapter = ReplyAdapter()
    val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
    val presenter:MessageConstract.ReplyPresenter = Presenter(this)
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
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        loadingView = DefaultUtil.createBottomView(context)
        refreshView = DefaultUtil.createTopView(context)
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

        presenter.refreshData()
    }
}