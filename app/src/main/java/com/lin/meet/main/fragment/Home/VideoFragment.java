package com.lin.meet.main.fragment.Home;

import android.graphics.Color;
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

public class VideoFragment extends HomeBaseFragment implements HomeConstract.VideoView {
    private View mView;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private HomeConstract.VideoPresenter presenter;
    LinearLayoutManager manager;

    private SuperSwipeRefreshLayout refresh;
    private View loadingView;
    private View refreshView;
    private View netError;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_video,container,false);
        return mView;
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
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    returnTop = false;
                    refresh.setRefreshing(true);
                    presenter.onInsertToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        netError  = (View)mView.findViewById(R.id.netError);
        loadingView = DefaultUtil.createBottomView(getContext());
        refreshView = DefaultUtil.createTopView(getContext());
        refresh = (SuperSwipeRefreshLayout)mView.findViewById(R.id.refresh);
        refresh.setHeaderViewBackgroundColor(Color.WHITE);
        refresh.setFooterView(loadingView);
        refresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                setLoadingViewStatus(loadingView,1);
                presenter.onInsertVideos();
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
        if(refresh.isRefreshing()){
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void endLoadMore() {
        refresh.setLoadMore(false);
    }

    @Override
    public void setNetError(boolean error) {
        netError.setVisibility(error?View.VISIBLE:View.GONE);
    }

    public void refresh(){
        presenter.onInitVideos(2);
    }


    @Override
    protected void loadData() {
        initVideo();
        presenter.onInitVideos(2);
    }

    boolean returnTop = false;
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
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
    }
}
