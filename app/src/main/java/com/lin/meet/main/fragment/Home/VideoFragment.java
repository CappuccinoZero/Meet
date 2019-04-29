package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.my_util.MyUtil;

public class VideoFragment extends Fragment implements HomeConstract.VideoView {
    private View mView;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private HomeConstract.VideoPresenter presenter;
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_video,container,false);
        initVideo();
        presenter.onInitVideos(0);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initVideo(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.video_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
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
    }

    public void refresh(Handler handler){
        this.handler = handler;
        presenter.onInitVideos(2);
    }


    public void insertVideo(String id){
        //presenter.onInsertVideo(id);
    }
}
