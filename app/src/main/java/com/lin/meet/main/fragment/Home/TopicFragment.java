package com.lin.meet.main.fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.lin.meet.R;
import com.lin.meet.bean.DefaultUtil;
import com.lin.meet.bean.TopSmoothScroller;

import java.util.Objects;

public class TopicFragment extends HomeBaseFragment implements HomeConstract.TopicView,TopicAdapter.TopicCallback{
    private View mView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private TopicAdapter adapter;
    private HomeConstract.TopicPresenter presenter;
    private boolean returnTop = false;
    private SuperSwipeRefreshLayout refresh;
    private View loadingView;
    private View refreshView;
    private View netError;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_topic,container,false);
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000 && resultCode == 2001){
            adapter.likeChange();
        }
    }

    private void setLoadingViewStatus(View loadingView,int flag){
        ((View)loadingView.findViewById(R.id.loading_1)).setVisibility(flag==1?View.VISIBLE:View.GONE);
        ((View)loadingView.findViewById(R.id.loading_2)).setVisibility(flag==2?View.VISIBLE:View.GONE);
        ((View)loadingView.findViewById(R.id.loading_3)).setVisibility(flag==3?View.VISIBLE:View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initTopic(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.topic_recyclerView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new TopicAdapter(this,this);
        mRecyclerView.setAdapter(adapter);
        presenter = new TopicPresenter(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    returnTop = false;
                    presenter.onInsertToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        netError  = (View)mView.findViewById(R.id.netError);
        loadingView = DefaultUtil.createBottomView(getContext());
        refreshView = DefaultUtil.createTopView(getContext());
        refresh = (SuperSwipeRefreshLayout)mView.findViewById(R.id.refresh);
        refresh.setFooterView(loadingView);
        refresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                setLoadingViewStatus(loadingView,1);
                presenter.onInsertTopics();
            }
            @Override
            public void onPushDistance(int i) { }
            @Override
            public void onPushEnable(boolean b) {
                if(b)setLoadingViewStatus(loadingView,3);
                else setLoadingViewStatus(loadingView,2);
            }
        });
        refresh.setHeaderView(refreshView);
        refresh.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                setLoadingViewStatus(refreshView,1);
                refresh();
            }

            @Override
            public void onPullDistance(int i) { }

            @Override
            public void onPullEnable(boolean b) {
                if(b) setLoadingViewStatus(refreshView,3);
                else setLoadingViewStatus(refreshView,2);
            }
        });
    }


    @Override
    public void initTopicAdapter() {
        adapter.initTopics();
    }

    @Override
    public int insertTopic(TopicAdapter.TopicBean bean) {
        return adapter.insertTopics(bean);
    }

    @Override
    public int insertTopic(int position, TopicAdapter.TopicBean bean) {
        return adapter.insertTopics(position,bean);
    }

    @Override
    public void endRefresh() {
        if(refresh.isRefreshing())
            refresh.setRefreshing(false);
    }

    @Override
    public void likeResult(int resultCode, int position, boolean like) {
        if(resultCode == 1){

        }
    }

    @Override
    public void setLike(int position, boolean like) {
        adapter.setLike(position,like);
    }

    @Override
    public void endLoadMore() {
        refresh.setLoadMore(false);
    }

    @Override
    public void isNetError(boolean error) {
        netError.setVisibility(error?View.VISIBLE:View.GONE);
    }

    public void insertTopic(String id){
        if(presenter!=null)
            presenter.onInsertTopic(id);
    }

    public void refresh(){
        presenter.onInitTopics(2);
    }

    @Override
    public void onClickLike(boolean like,String id,String uid,int position) {
        presenter.onClickLike(like,id,uid,position);
    }

    @Override
    protected void loadData() {
        initTopic();
        refresh.setRefreshing(true);
        presenter.onInitTopics(2);
    }

    public void scrollAndRefresh(){
        doRecyclerScroll();
        if(manager.findFirstVisibleItemPosition()<=4){
            refresh.setRefreshing(true);
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()->presenter.onInsertToTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            returnTop = true;
        }
    }
    public void doRecyclerScroll(){
        if(getActivity()!=null){
            TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
            scroller.setTargetPosition(0);
            manager.startSmoothScroll(scroller);
        }
    }
}
