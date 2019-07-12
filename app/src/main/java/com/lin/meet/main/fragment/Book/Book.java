package com.lin.meet.main.fragment.Book;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.Baike;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.bean.User;
import com.lin.meet.main.MainConstract;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.recommend.RecommendConstract;

import org.jetbrains.annotations.NotNull;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class Book extends Fragment implements BaikeConstract.View, RecommendConstract.searchCallback {
    private View mView = null;
    private BaikeConstract.Presenter presenter;
    private View search;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BaikeAdapter adapter;
    private SwipeRefreshLayout refresh;
    private boolean stopLoad = false;
    private boolean returnTop = false;
    private int MAX_TRANSLATION_Y;
    private CircleImageView header;
    private MainConstract.MainDrawerCallback callback;
    private AppBarLayout appBarLayout;
    private View stateBar;
    private boolean showStateBar = false;

    private int image_height = 0;
    private int header_translationY = 0;
    private int search_translationY = 0;
    private int search_scaleX = 0;
    private float search_scale = 0;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_book, container, false);
        initView(mView);
        initDimenFromResource();
        refresh.setRefreshing(true);
        presenter.onRefreshBaike();
        return mView;
    }

    public void setDrawerCallback(MainConstract.MainDrawerCallback callback){
        this.callback = callback;
    }

    private void initDimenFromResource(){
        header_translationY = (int) getActivity().getResources().getDimension(R.dimen.header_translationY);
        search_scaleX = (int) getActivity().getResources().getDimension(R.dimen.search_scaleX);
        search_translationY = (int) getActivity().getResources().getDimension(R.dimen.search_translationY);
        image_height = (int) getActivity().getResources().getDimension(R.dimen.image_height);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view){
        MAX_TRANSLATION_Y = (int)getActivity().getResources().getDimension(R.dimen.recomMaxTranslationY);
        presenter = new BaikePresenter(this);
        appBarLayout = (AppBarLayout)view.findViewById(R.id.appbarLayout);
        header = (CircleImageView)view.findViewById(R.id.header);
        recyclerView = (RecyclerView)view.findViewById(R.id.baike_recycler);
        refresh = (SwipeRefreshLayout)view.findViewById(R.id.baike_refresh);
        search = (View)view.findViewById(R.id.baike_search);
        stateBar = (View)mView.findViewById(R.id.mStateBar);
        refresh.setColorSchemeResources(R.color.refresh_color);
        stateBar.setAlpha(0);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter = new BaikeAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)&&!stopLoad){
                    adapter.setLoadingStatus(true);
                    presenter.onInsertBaike();
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    appBarLayout.setExpanded(true);
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
            Pair<View,String> pair = new Pair<>(search, "baike_search");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),pair);
            startActivityForResult(new Intent(getActivity(),BaikeSearch.class),1001,compat.toBundle());
        });
        adapter.setHideCallback(this);

        search.post(()-> {
            search_scale = search_scaleX/(float)search.getWidth();
            search.setPivotX(search.getWidth());
        });
        header.post(()-> {
            header.setPivotX(header.getWidth()/2f);
            header.setPivotY(header.getHeight()/2f);
        });
        header.setOnClickListener(v->callback.openDrawer());

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                float d = i/(float)image_height;
                header.setTranslationY(d*header_translationY);
                search.setTranslationY(d*search_translationY);
                d = (float) Math.sqrt(Math.abs(d));
                header.setScaleX(1f-Math.abs((1/8f)*d));
                header.setScaleY(1f-Math.abs((1/8f)*d));
                search.setScaleX(1f-search_scale * d);
                if(Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    showMyStateBar();
                }else{
                    hideMyStateBar();
                }
            }
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scrollAndRefresh(){
        if(refresh.isRefreshing())return;
        TopSmoothScroller scroller = new TopSmoothScroller(getActivity());
        scroller.setTargetPosition(0);
        manager.startSmoothScroll(scroller);
        refresh.setRefreshing(true);
        returnTop = true;
        if(manager.findFirstVisibleItemPosition()<4){
            new Thread(()->{
                try {
                    Thread.sleep(600);
                    getActivity().runOnUiThread(()->{presenter.onInsertBaikeToTop();});
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void insertBaikeToTop(@NotNull int position,Baike baike) {
        adapter.insertBaikeToTop(position,baike);
    }

    @Override
    public void setVisiable(boolean visiable) {

    }

    @Override
    public void onResume() {
        super.onResume();
        initHeader();
        setVisiable(true);
    }

    private void initHeader(){
        if(BmobUser.isLogin()&&!BmobUser.getCurrentUser(User.class).getHeaderUri().isEmpty()){
            Glide.with(this).load(BmobUser.getCurrentUser(User.class).getHeaderUri()).into(header);
        }else{
            Glide.with(this).load(R.drawable.header).into(header);
        }
    }

    @Override
    public void endLoading() {
        if(adapter.isLoading())
            adapter.setLoadingStatus(false);
    }

    @Override
    public void error() {
        if(adapter.getItemCount()<=1){
            adapter.setError(true);
        }
        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initError() {
        if(adapter.getItemCount()<=1){
            adapter.setError(false);
        }
    }

    private void hideMyStateBar(){
        if(showStateBar||stateBar.getAlpha()!=0){
            showStateBar = false;
            stateBar.setAlpha(0);
        }
    }

    private void showMyStateBar(){
        if(!showStateBar){
            showStateBar = true;
            ObjectAnimator animator = ObjectAnimator.ofFloat(stateBar,"Alpha",stateBar.getAlpha(),1f);
            animator.setDuration(400);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(!showStateBar)
                        stateBar.setAlpha(0);
                }
            });
            animator.start();
        }
    }
}
