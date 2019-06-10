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
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.my_util.MyUtil;

import java.util.Objects;
import java.util.Random;

public class RecommendFragment extends HomeBaseFragment {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LoveNews.JSOUP_NEWS_MESSAGE:
                    if(mAdapter!=null){
                        Bundle data = msg.getData();
                        LoveNewsBean bean = (LoveNewsBean) data.getSerializable("LoveNews");
                        mAdapter.addRecomds(bean);
                        if(pHandler!=null){
                            Message msg2 = new Message();
                            msg2.what = Home.END_REFRESH;
                            pHandler.sendMessage(msg2);
                            pHandler = null;
                        }
                    }
                    break;
                case LoveNews.JSOUP_NEWS_MESSAGE_TOP:
                    if(mAdapter!=null){
                        Bundle data = msg.getData();
                        LoveNewsBean bean = (LoveNewsBean) data.getSerializable("LoveNews");
                        mAdapter.addRecomds(page++,bean);
                        if(pHandler!=null){
                            Message msg2 = new Message();
                            msg2.what = Home.END_REFRESH;
                            pHandler.sendMessage(msg2);
                            pHandler = null;
                        }
                    }
                    break;
            }
        }
    };
    private Handler pHandler;
    private int lovePage;
    private int paged = 1;
    private View mView;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;
    private LinearLayoutManager manager;
    private int page = 0;
    private boolean returnTop = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_recommend, container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRecommend(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.re_recyclerView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new RecommendAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    if(paged<=3){
                        lovePage++;
                        paged++;
                        int temp = lovePage%3;
                        if(temp==0)
                            temp = 3;
                        LoveNews.updateNews(handler,temp,false);
                    }
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    doRefresh.doRefresh();
                    returnTop = false;
                    loadTopData();
                }
            }
        });
    }

    public void refresh(Handler handler){
        pHandler = handler;
        mAdapter.refresh();
        paged = 1;
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(this.handler,lovePage,false);
    }

    @Override
    protected void loadData() {
        initRecommend();
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(handler,lovePage,false);
    }

    private void loadTopData(){
        initRecommend();
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(handler,lovePage,true);
    }

    public void scrollAndRefresh(Handler handler,ReRefreshCallback doRefresh){
        if(pHandler!=null) return;
        pHandler = handler;
        doRecyclerScroll();
        if(manager.findFirstVisibleItemPosition()<=4){
            doRefresh.doRefresh();
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(this::loadTopData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            this.doRefresh = doRefresh;
            returnTop = true;
        }
        page = 0;
    }

    ReRefreshCallback doRefresh;
    public interface ReRefreshCallback{
        void doRefresh();
    }

    public void doRecyclerScroll(){
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
    }
}
