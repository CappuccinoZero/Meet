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
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.my_util.MyUtil;

public class RecommendFragment extends Fragment {
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
                    }
                    break;
            }
        }
    };
    private long time ;
    private int lovePage = 2;
    private View mView;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_recommend, container,false);
        initRecommend();
        LoveNews.updateNews(handler,1);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRecommend(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.re_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new RecommendAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    if(lovePage<=3&&System.currentTimeMillis()-time>=3*1000){
                        time = System.currentTimeMillis();
                        LoveNews.updateNews(handler,lovePage);
                        lovePage++;
                    }
                }
            }
        });
    }
}
