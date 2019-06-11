package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.bean.User;
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.main.MainConstract;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.recommend.RecommendConstract;

import java.util.Objects;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendFragment extends Fragment implements RecommendConstract.searchCallback {
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
                        if(refresh.isRefreshing())
                            refresh.setRefreshing(false);
                    }
                    break;
                case LoveNews.JSOUP_NEWS_MESSAGE_TOP:
                    Log.d("测试", "handleMessage: 2");
                    if(mAdapter!=null){
                        Bundle data = msg.getData();
                        LoveNewsBean bean = (LoveNewsBean) data.getSerializable("LoveNews");
                        mAdapter.addRecomds(page++,bean);
                        if(refresh.isRefreshing())
                            refresh.setRefreshing(false);
                    }
                    break;
            }
        }
    };
    private MainConstract.MainDrawerCallback callback;
    private int MAX_TRANSLATION_Y;
    private View testView;
    private int lovePage;
    private int paged = 1;
    private View mView;
    private ImageView menu;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout refresh;
    private CircleImageView header;
    private int page = 0;
    private boolean returnTop = false;
    public void setDrawerCallback(MainConstract.MainDrawerCallback callback){
        this.callback = callback;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_recommend, container,false);
        initRecommend();
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(handler,lovePage,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRecommend(){
        MAX_TRANSLATION_Y = (int)getActivity().getResources().getDimension(R.dimen.recomMaxTranslationY);
        menu = (ImageView)mView.findViewById(R.id.menu);
        refresh = (SwipeRefreshLayout)mView.findViewById(R.id.refresh);
        testView = (View)mView.findViewById(R.id.testView);
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.re_recyclerView);
        header = (CircleImageView)mView.findViewById(R.id.header);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new RecommendAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        refresh.setProgressViewOffset(true,
                (int)getActivity().getResources().getDimension(R.dimen.refresh_start),
                (int)getActivity().getResources().getDimension(R.dimen.refresh_end));
        mAdapter.setHideCallback(this);
        refresh.setOnRefreshListener(this::refresh);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int temp = (int)(testView.getTranslationY()-dy);
                if(temp<-MAX_TRANSLATION_Y)temp = -MAX_TRANSLATION_Y;
                else if(temp>0)temp = 0;
                testView.setTranslationY(temp);
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    if(paged<=3){
                        lovePage++;
                        paged++;
                        temp = lovePage%3;
                        if(temp==0)
                            temp = 3;
                        LoveNews.updateNews(handler,temp,false);
                    }
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    refresh.setRefreshing(true);
                    returnTop = false;
                    loadTopData();
                }
            }
        });
        menu.setOnClickListener(v->callback.openDrawer());
    }

    public void refresh(){
        mAdapter.refresh();
        paged = 1;
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(this.handler,lovePage,false);
    }

    private void loadTopData(){
        Random random = new Random();
        lovePage = 1 + random.nextInt(3);
        LoveNews.updateNews(handler,lovePage,true);
    }

    public void scrollAndRefresh(){
        doRecyclerScroll();
        if(manager.findFirstVisibleItemPosition()<=4){
            refresh.setRefreshing(true);
            new Thread(()->{
                try {
                    Thread.sleep(800);
                    Objects.requireNonNull(getActivity()).runOnUiThread(this::loadTopData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }else
            returnTop = true;
        page = 0;
    }

    @Override
    public void setVisiable(boolean visiable) {
        if(testView!=null){
            testView.setVisibility(visiable?View.VISIBLE:View.INVISIBLE);
        }
    }

    public interface ReRefreshCallback{
        void doRefresh();
    }

    public void doRecyclerScroll(){
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BmobUser.isLogin()&&header!=null){
            String url = BmobUser.getCurrentUser(User.class).getHeaderUri();
            Glide.with(getActivity()).load(url).into(header);
        }
        setVisiable(true);
    }
}
