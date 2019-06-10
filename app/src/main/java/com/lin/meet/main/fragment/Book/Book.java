package com.lin.meet.main.fragment.Book;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lin.meet.R;
import com.lin.meet.bean.Baike;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

public class Book extends Fragment implements BaikeConstract.View{
    private View mView = null;
    private BaikeConstract.Presenter presenter;
    private LinearLayout search;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BaikeAdapter adapter;
    private SwipeRefreshLayout refresh;
    private boolean stopLoad = false;
    private boolean returnTop = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_book, container, false);
        initView(mView);
        presenter.onRefreshBaike();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view){
        presenter = new BaikePresenter(this);
        recyclerView = (RecyclerView)view.findViewById(R.id.baike_recycler);
        refresh = (SwipeRefreshLayout)view.findViewById(R.id.baike_refresh);
        search = (LinearLayout)view.findViewById(R.id.baike_search);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter = new BaikeAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)&&!stopLoad){
                    presenter.onInsertBaike();
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    returnTop = false;
                    presenter.onInsertBaikeToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        refresh.setOnRefreshListener(()->{
            stopLoad = true;
            presenter.onRefreshBaike();
        });

        search.setOnClickListener((v)->{
            getActivity().getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
            Pair<View,String> pair = new Pair<>(search, ViewCompat.getTransitionName(search));
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),pair);
            startActivityForResult(new Intent(getActivity(),BaikeSearch.class),1001,compat.toBundle());
        });
    }

    @Override
    public void initAdapter() {
        adapter.refreshAdapter();
    }

    @Override
    public void insertBaike(@NotNull Baike baike) {
        adapter.insertBaike(baike);
    }

    @Override
    public void endRefresh() {
        stopLoad = false;
        if(refresh.isRefreshing())
            refresh.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001&&resultCode==1001){
            presenter.onSearch(data.getStringExtra("Search"));
            Log.d("测试", "onActivityResult: 返回成功");
        }
        Log.d("测试", "onActivityResult: guoqu");
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scrollAndRefresh(){
        if(refresh.isRefreshing())return;
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
        refresh.setRefreshing(true);
        if(manager.findFirstVisibleItemPosition()<4){
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    getActivity().runOnUiThread(()->{presenter.onInsertBaikeToTop();});
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            returnTop = true;
        }
    }

    @Override
    public void insertBaikeToTop(@NotNull int position,Baike baike) {
        adapter.insertBaikeToTop(position,baike);
    }
}
