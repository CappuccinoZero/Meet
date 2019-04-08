package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;

public class VideoFragment extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_video,container,false);
        initVideo();
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
        for(int i=0;i<10;i++)
            mAdapter.insertData(new VideoBean("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        mRecyclerView.setAdapter(mAdapter);
    }
}
