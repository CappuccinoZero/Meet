package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.my_util.MyUtil;

import java.util.Objects;

public class TopicFragment extends HomeBaseFragment implements HomeConstract.TopicView,TopicAdapter.TopicCallback{
    private View mView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private TopicAdapter adapter;
    private HomeConstract.TopicPresenter presenter;
    private Handler handler;
    private boolean returnTop = false;
    RecommendFragment.ReRefreshCallback doRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_topic,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initTopic(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.topic_recyclerView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new TopicAdapter(this);
        mRecyclerView.setAdapter(adapter);
        presenter = new TopicPresenter(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    presenter.onInsertTopics();
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    returnTop = false;
                    presenter.onInsertToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
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
        Message msg = new Message();
        msg.what = Home.END_REFRESH;
        handler.sendMessage(msg);
        handler = null;
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

    public void insertTopic(String id){
        if(presenter!=null)
            presenter.onInsertTopic(id);
    }

    public void refresh(Handler handler){
        this.handler = handler;
        presenter.onInitTopics(2);
    }

    @Override
    public void onClickLike(int position, String id) {
        presenter.onClickLike(position,id);
    }

    @Override
    protected void loadData() {
        initTopic();
        presenter.onInitTopics(0);
    }

    public void scrollAndRefresh(Handler handler, RecommendFragment.ReRefreshCallback doRefresh){
        if (this.handler!=null) return;
        this.handler = handler;
        doRecyclerScroll();
        if(manager.findFirstVisibleItemPosition()<=4){
            doRefresh.doRefresh();
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()->presenter.onInsertToTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            this.doRefresh = doRefresh;
            returnTop = true;
        }
    }
    public void doRecyclerScroll(){
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
    }
}
