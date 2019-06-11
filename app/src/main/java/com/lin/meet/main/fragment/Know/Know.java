package com.lin.meet.main.fragment.Know;

import android.content.Intent;
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
import android.widget.TextView;

import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.main.fragment.Home.Home;
import com.lin.meet.main.fragment.Home.HomeBaseFragment;
import com.lin.meet.main.fragment.Home.RecommendFragment;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
//startActivityForResult(new Intent(getActivity(), SendKnowActivity.class),2001);
public class Know extends HomeBaseFragment implements KnowConstarct.View {
    private View mView = null;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager manager;
    private KnowAdapter adapter;
    private TextView know;
    private KnowConstarct.Presenter presenter;
    private boolean returnTop = false;
    private Handler handler = null;
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

    private void initView(View view){
        presenter = new KnowPresenter(this);
        recyclerView = view.findViewById(R.id.know_recycler);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new KnowAdapter();
        recyclerView.setAdapter(adapter);
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
                    doRefresh.doRefresh();
                    presenter.onInsertKnowToTop();
                }
                super.onScrolled(recyclerView, dx, dy);
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
        if(handler!=null){
            Message msg = new Message();
            msg.what = Home.END_REFRESH;
            handler.sendMessage(msg);
            handler = null;
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

    RecommendFragment.ReRefreshCallback doRefresh;
    public void scrollAndRefresh(Handler handler, RecommendFragment.ReRefreshCallback doRefresh){
        if (handler==null)return;
        this.handler = handler;
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
        int positions[] = null;
        positions = manager.findFirstVisibleItemPositions(positions);
        if(positions!=null&&positions[0]<=4){
            doRefresh.doRefresh();
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    Objects.requireNonNull(getActivity()).runOnUiThread(()-> presenter.onInsertKnowToTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            this.doRefresh = doRefresh;
            returnTop = true;
        }
    }

    public void refresh(Handler handler){
        this.handler = handler;
        presenter.refreshKnows();
    }

    @Override
    protected void loadData() {
        initView(mView);
        presenter.refreshKnows();
    }
}
