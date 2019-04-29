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

public class TopicFragment extends Fragment implements HomeConstract.TopicView,TopicAdapter.TopicCallback{
    private View mView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private TopicAdapter adapter;
    private HomeConstract.TopicPresenter presenter;
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_topic,container,false);
        initTopic();
        presenter.onInitTopics(0);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initTopic(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.topic_recyclerView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new TopicAdapter(this);
        mRecyclerView.setAdapter(adapter);
        presenter = new TopicPresenter(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    presenter.onInsertTopics();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void initTopicAdapter() {
        adapter.initTopics();
    }

    @Override
    public int insertTopic(TopicAdapter.TopicBean bean) {
        return adapter.insertTopics(bean);
    }

    @Override
    public int insertTopic(int position, TopicAdapter.TopicBean bean) {
        return adapter.insertTopics(position,bean);
    }

    @Override
    public void endRefresh() {
        Message msg = new Message();
        msg.what = Home.END_REFRESH;
        handler.sendMessage(msg);
    }

    @Override
    public void likeResult(int resultCode, int position, boolean like) {
        if(resultCode == 1){

        }
    }

    @Override
    public void setLike(int position, boolean like) {
        adapter.setLike(position,like);
    }

    public void insertTopic(String id){
        presenter.onInsertTopic(id);
    }

    public void refresh(Handler handler){
        this.handler = handler;
        presenter.onInitTopics(2);
    }

    @Override
    public void onClickLike(int position, String id) {
        presenter.onClickLike(position,id);
    }
}
