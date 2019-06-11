package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.bean.video_main;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PictureFragment extends HomeBaseFragment implements PictureContract.View {
    private View mView;
    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapter;
    private PictureContract.Presenter presenter;
    private Handler handler = null;
    StaggeredGridLayoutManager manager;
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


    private void initPictures(){
        presenter = new PicturePresenter(this);
        mAdapter = new PicturesAdapter(getActivity());
        mRecyclerView= (RecyclerView)mView.findViewById(R.id.pictures_recyclerView);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){//底部
                    presenter.insertPictures();
                }
                int positions[] = null;
                positions = manager.findFirstVisibleItemPositions(positions);
                if(returnTop&&positions!=null&&positions[0]==0){
                    returnTop = false;
                    doRefresh.doRefresh();
                    presenter.insertToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void loadData() {
        initPictures();
        presenter.initPictures();
    }

    @Override
    public void insertPictures(@NotNull video_main bean) {
        mAdapter.insertPicture(bean);
    }

    @Override
    public void clanPictures() {
        mAdapter.refreshPicture();
    }

    @Override
    public void stopRefresh() {
        if(handler != null){
            Message msg = new Message();
            msg.what = Home.END_REFRESH;
            handler.sendMessage(msg);
            handler = null;
        }
    }

    public void doRefresh(Handler handler){
        this.handler = handler;
        presenter.refreshPictures();
    }

    private RecommendFragment.ReRefreshCallback doRefresh;
    private boolean returnTop = false;
    public void scrollAndRefresh(Handler handler, RecommendFragment.ReRefreshCallback doRefresh){
        if(this.handler != null)
            return;
        this.handler = handler;
        doRecyclerScroll();
        int positions[] = null;
        positions = manager.findFirstVisibleItemPositions(positions);
        if(positions!=null&&positions[0]<=4){
            doRefresh.doRefresh();
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()-> presenter.insertToTop());
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

    @Override
    public void insertPictures(int posiotion, @NotNull video_main bean) {
        mAdapter.insertPicture(posiotion,bean);
    }
}
