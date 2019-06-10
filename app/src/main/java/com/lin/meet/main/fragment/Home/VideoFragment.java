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

public class VideoFragment extends HomeBaseFragment implements HomeConstract.VideoView {
    private View mView;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private HomeConstract.VideoPresenter presenter;
    private Handler handler;
    LinearLayoutManager manager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_video,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initVideo(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.video_recyclerView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new VideoAdapter();
        mRecyclerView.setAdapter(mAdapter);
        presenter = new VideoPresenter(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    presenter.onInsertVideos();
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
    public void refreshVideos() {
        mAdapter.initVideos();
    }

    @Override
    public void insertVideo(int position, VideoBean bean) {
        mAdapter.insertVideo(position,bean);
    }

    @Override
    public void insertVideo(VideoBean bean) {
        mAdapter.insertVideo(bean);
    }

    @Override
    public void endRefresh() {
        Message msg = new Message();
        msg.what = Home.END_REFRESH;
        handler.sendMessage(msg);
        handler = null;
    }

    public void refresh(Handler handler){
        this.handler = handler;
        presenter.onInitVideos(2);
    }


    public void insertVideo(String id){
        //presenter.onInsertVideo(id);
    }

    @Override
    protected void loadData() {
        initVideo();
        presenter.onInitVideos(0);
    }

    boolean returnTop = false;
    RecommendFragment.ReRefreshCallback doRefresh;
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
