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
import com.lin.meet.bean.video_main;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

public class PictureFragment extends HomeBaseFragment implements PictureContract.View {
    private View mView;
    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapter;
    private PictureContract.Presenter presenter;
    private Handler handler = null;
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
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){//底部
                    presenter.insertPictures();
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
        }
    }

    public void doRefresh(Handler handler){
        this.handler = handler;
        presenter.refreshPictures();
    }
}
