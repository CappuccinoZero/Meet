package com.lin.meet.main.fragment.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.lin.meet.R;
import com.lin.meet.bean.DefaultUtil;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.db_bean.picture_main;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PictureFragment extends HomeBaseFragment implements PictureContract.View {
    private View mView;
    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapter;
    private PictureContract.Presenter presenter;
    private StaggeredGridLayoutManager manager;

    private SuperSwipeRefreshLayout refresh;
    private View loadingView;
    private View refreshView;
    private View netError;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_pictures,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setLoadingViewStatus(View loadingView,int flag){
        ((View)loadingView.findViewById(R.id.loading_1)).setVisibility(flag==1?View.VISIBLE:View.GONE);
        ((View)loadingView.findViewById(R.id.loading_2)).setVisibility(flag==2?View.VISIBLE:View.GONE);
        ((View)loadingView.findViewById(R.id.loading_3)).setVisibility(flag==3?View.VISIBLE:View.GONE);
    }

    private void initPictures(){
        presenter = new PicturePresenter(this);
        mAdapter = new PicturesAdapter(getActivity());
        netError  = (View)mView.findViewById(R.id.netError);
        mRecyclerView= (RecyclerView)mView.findViewById(R.id.pictures_recyclerView);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int positions[] = null;
                positions = manager.findFirstVisibleItemPositions(positions);
                if(returnTop&&positions!=null&&positions[0]==0){
                    returnTop = false;
                    refresh.setRefreshing(true);
                    presenter.insertToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        loadingView = DefaultUtil.createBottomView(getContext());
        refreshView = DefaultUtil.createTopView(getContext());
        refresh = (SuperSwipeRefreshLayout)mView.findViewById(R.id.refresh);
        refresh.setHeaderViewBackgroundColor(Color.WHITE);
        refresh.setFooterView(loadingView);
        refresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                setLoadingViewStatus(loadingView,1);
                presenter.insertPictures();
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
                doRefresh();
                presenter.refreshPictures();
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
    protected void loadData() {
        initPictures();
        refresh.setRefreshing(true);
        presenter.initPictures();
    }

    @Override
    public void insertPictures(@NotNull picture_main bean) {
        mAdapter.insertPicture(bean);
    }

    @Override
    public void clanPictures() {
        mAdapter.refreshPicture();
    }

    @Override
    public void stopRefresh() {
        if(refresh.isRefreshing()){
            refresh.setRefreshing(false);
        }
    }

    public void doRefresh(){
        presenter.refreshPictures();
    }

    private boolean returnTop = false;
    public void scrollAndRefresh(){
        doRecyclerScroll();
        int positions[] = null;
        positions = manager.findFirstVisibleItemPositions(positions);
        if(positions!=null&&positions[0]<=4){
            refresh.setRefreshing(true);
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()-> presenter.insertToTop());
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

    @Override
    public void insertPictures(int posiotion, @NotNull picture_main bean) {
        mAdapter.insertPicture(posiotion,bean);
    }

    @Override
    public void endLoadMore() {
        refresh.setLoadMore(false);
    }

    @Override
    public void setNetError(boolean error) {
        netError.setVisibility(error?View.VISIBLE:View.GONE);
    }

}
