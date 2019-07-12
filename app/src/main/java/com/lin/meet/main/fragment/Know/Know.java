package com.lin.meet.main.fragment.Know;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.lin.meet.R;
import com.lin.meet.bean.DefaultUtil;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.main.fragment.Home.HomeBaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
//startActivityForResult(new Intent(getActivity(), SendKnowActivity.class),2001);
public class Know extends HomeBaseFragment implements KnowConstarct.View {
    private View mView = null;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private KnowAdapter adapter;
    private TextView know;
    private KnowConstarct.Presenter presenter;

    private SuperSwipeRefreshLayout refresh;
    private View loadingView;
    private View refreshView;
    private boolean returnTop = false;
    private View netError;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_know, container, false);
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

    private void initView(View view){
        presenter = new KnowPresenter(this);
        netError  = (View)mView.findViewById(R.id.netError);
        recyclerView = view.findViewById(R.id.know_recycler);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter = new KnowAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int positions = manager.findFirstVisibleItemPosition();
                if(positions==0&&returnTop){
                    returnTop = false;
                    refresh.setRefreshing(true);
                    presenter.onInsertKnowToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        loadingView = DefaultUtil.createBottomView(getContext());
        refreshView = DefaultUtil.createTopView(getContext());
        refresh = (SuperSwipeRefreshLayout)mView.findViewById(R.id.refresh);
        refresh.setFooterView(loadingView);
        refresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                setLoadingViewStatus(loadingView,1);
                presenter.insetKnow();
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
    public void refreshAdapter() {
        adapter.refresh();
    }

    @Override
    public void insertKnow(@NotNull KnowAndUser bean) {
        adapter.insertKnow(bean);
    }

    @Override
    public void endRefresh() {
        if(refresh.isRefreshing()){
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void insertKnow(int position, @NotNull KnowAndUser bean) {
        adapter.insertKnow(position,bean);
        manager.scrollToPosition(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==2001&&resultCode==2001){
            String id = data.getStringExtra("ID");
            presenter.insertKnow(id);
        }
        else if(requestCode==2000&&resultCode==2001){
            adapter.changeOk();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scrollAndRefresh(){
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
        int positions = manager.findFirstVisibleItemPosition();
        if(positions<=4){
            refresh.setRefreshing(true);
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()-> presenter.onInsertKnowToTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            returnTop = true;
        }
    }

    public void refresh(){
        presenter.refreshKnows();
    }

    @Override
    protected void loadData() {
        initView(mView);
        refresh.setRefreshing(true);
        presenter.refreshKnows();
    }

    @Override
    public void endLoadMore() {
        refresh.setLoadMore(false);
    }

    @Override
    public void isNetError(boolean error) {
        netError.setVisibility(error?View.VISIBLE:View.GONE);
    }
}
