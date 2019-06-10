package com.lin.meet.main.fragment.Know;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lin.meet.Know.SendKnowActivity;
import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Know extends Fragment implements View.OnClickListener,KnowConstarct.View {
    private View mView = null;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager manager;
    private KnowAdapter adapter;
    private SwipeRefreshLayout refresh;
    private TextView know;
    private KnowConstarct.Presenter presenter;
    private boolean returnTop = false;
    private boolean refreshing = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_know, container, false);
        initView(mView);
        presenter.refreshKnows();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view){
        presenter = new KnowPresenter(this);
        recyclerView = view.findViewById(R.id.know_recycler);
        refresh = view.findViewById(R.id.know_refresh);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new KnowAdapter();
        recyclerView.setAdapter(adapter);
        know = (TextView)view.findViewById(R.id.know_text);
        know.setOnClickListener(this);
        refresh.setOnRefreshListener(()->{
            presenter.refreshKnows();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    presenter.insetKnow();
                }
                int positions[] = null;
                positions = manager.findFirstVisibleItemPositions(positions);
                if(returnTop&&positions!=null&&positions[0]==0){
                    returnTop = false;
                    presenter.onInsertKnowToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.know_text:
                startActivityForResult(new Intent(getActivity(), SendKnowActivity.class),2001);
                break;
        }
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
            refreshing = false;
        }
    }

    public void insertKnow(String id){

    }

    @Override
    public void insertKnow(int position, @NotNull KnowAndUser bean) {
        adapter.insertKnow(position,bean);
        manager.scrollToPosition(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2001&&resultCode==2001){
            String id = data.getStringExtra("ID");
            presenter.insertKnow(id);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scrollAndRefresh(){
        if (refreshing)return;
        refreshing = true;
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
        refresh.setRefreshing(true);
        int positions[] = null;
        positions = manager.findFirstVisibleItemPositions(positions);
        if(positions!=null&&positions[0]<=4){
            new Thread(()->{
                try {
                    Thread.sleep(800);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()-> presenter.onInsertKnowToTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            returnTop = true;
        }
    }
}
