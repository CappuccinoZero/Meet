package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.my_util.MyUtil;

public class PictureFragment extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapter;
    private HomeContract.View parentView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_pictures,container,false);
        initPictures();
        //if(mAdapter.initLoad)
            //parentView.isShowLoad(true);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setParentView(HomeContract.View parentView){
        this.parentView = parentView;
    }

    private void initPictures(){
        mAdapter = new PicturesAdapter(getActivity());
        mRecyclerView= (RecyclerView)mView.findViewById(R.id.pictures_recyclerView);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(manager);
        if(parentView!=null)
            mAdapter.parentView = parentView;
        mAdapter.start();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){//底部
                    mAdapter.insertData(getActivity());
                }
                mAdapter.roll_dy = dy;
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mAdapter.setRolling(false);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mAdapter.setRolling(true);
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public boolean getinitLoadStatus(){
        return mAdapter.initLoad;
    }

    public void refreshPictures(){
        mAdapter.refresh(null);
    }
}
