package com.lin.meet.main.fragment.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.TopSmoothScroller;
import com.lin.meet.bean.User;
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.main.MainConstract;
import com.lin.meet.main.MainSearchActivity;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.recommend.RecommendConstract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                        mAdapter.addRecomd(bean);
                        if(mAdapter.isError())
                            mAdapter.setError(false);
                        if(refresh.isRefreshing())
                            refresh.setRefreshing(false);
                        if(loading)
                            loading = false;
                        if(mAdapter.isLoading())
                            mAdapter.setLoadingStatus(false);
                        if(firstLoading)
                            firstLoading = false;
                    }
                    break;
                case LoveNews.JSOUP_NEWS_MESSAGE_TOP:
                    if(mAdapter!=null){
                        Bundle data = msg.getData();
                        LoveNewsBean bean = (LoveNewsBean) data.getSerializable("LoveNews");
                        mAdapter.addRecomd(page++,bean);
                        if(mAdapter.isError())
                            mAdapter.setError(false);
                        if(refresh.isRefreshing())
                            refresh.setRefreshing(false);
                        if(loading)
                            loading = false;
                        if(mAdapter.isLoading())
                            mAdapter.setLoadingStatus(false);
                        if(firstLoading)
                            firstLoading = false;
                    }
                    break;
                case LoveNews.JSOUP_CONNECT_ERROR:
                    Toast.makeText(getContext(),"连接错误！！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private View stateBar;
    private boolean showStateBar = false;
    private AppBarLayout appbar;
    private MainConstract.MainDrawerCallback callback;
    private int MAX_TRANSLATION_Y;
    private View searchView;
    private View mView;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout refresh;
    private CircleImageView header;
    private int page = 0;
    private boolean returnTop = false;
    private boolean loveError = false;

    private boolean firstLoading = true;
    private int image_height = 0;
    private int header_translationY = 0;
    private int search_translationY = 0;
    private int search_scaleX = 0;
    private float search_scale = 0;
    public void setDrawerCallback(MainConstract.MainDrawerCallback callback){
        this.callback = callback;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.home_recommend, container,false);
        initRecommend();
        initDimenFromResource();
        refresh.setRefreshing(true);
        getNetData(false,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRecommend(){
        MAX_TRANSLATION_Y = (int)getActivity().getResources().getDimension(R.dimen.recomMaxTranslationY);
        refresh = (SwipeRefreshLayout)mView.findViewById(R.id.refresh);
        searchView = (View)mView.findViewById(R.id.searchView);
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.re_recyclerView);
        appbar = (AppBarLayout)mView.findViewById(R.id.recommend_appbarLayout);
        header = (CircleImageView)mView.findViewById(R.id.header);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new RecommendAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setHideCallback(this);
        refresh.setOnRefreshListener(this::refresh);
        stateBar = (View)mView.findViewById(R.id.mStateBar);
        stateBar.setAlpha(0);
        refresh.setColorSchemeResources(R.color.refresh_color);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(MyUtil.isSlidetoBottom(recyclerView)&&!mAdapter.isLoading()&&!firstLoading){
                    mAdapter.setLoadingStatus(true);
                    getNetData(true,false);
                }
                if(returnTop&&manager.findFirstVisibleItemPosition()==0){
                    appbar.setExpanded(true);
                    refresh.setRefreshing(true);
                    returnTop = false;
                    loadTopData();
                }
                Log.d("测试", "showStateBar: 显示");
            }
        });
        searchView.setOnClickListener(v->{
            getActivity().getWindow().setExitTransition(new Explode());
            Pair<View,String> pair = new Pair<>(searchView,"search");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),pair);
            Intent intent = new Intent(getActivity(), MainSearchActivity.class);
            startActivity(intent,compat.toBundle());
        });
        searchView.post(()-> {
            search_scale = search_scaleX/(float)searchView.getWidth();
            searchView.setPivotX(searchView.getWidth());
        });
        header.post(()-> {
            header.setPivotX(header.getWidth()/2f);
            header.setPivotY(header.getHeight()/2f);
        });
        header.setOnClickListener(v->callback.openDrawer());

        appbar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                float d = i/(float)image_height;
                header.setTranslationY(d*header_translationY);
                searchView.setTranslationY(d*search_translationY);
                d = (float) Math.sqrt(Math.abs(d));
                header.setScaleX(1f-Math.abs((1/8f)*d));
                header.setScaleY(1f-Math.abs((1/8f)*d));
                searchView.setScaleX(1f-search_scale * d);
                if(Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    showMyStateBar();
                }else{
                    hideMyStateBar();
                }
            }
        });
    }

    private void initDimenFromResource(){
        header_translationY = (int) getActivity().getResources().getDimension(R.dimen.header_translationY);
        search_scaleX = (int) getActivity().getResources().getDimension(R.dimen.search_scaleX);
        search_translationY = (int) getActivity().getResources().getDimension(R.dimen.search_translationY);
        image_height = (int) getActivity().getResources().getDimension(R.dimen.image_height);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void refresh(){
        lovehhyPage = 0;
        aidongwuPage = 0;
        mAdapter.refresh();
        getNetData(true,false);
    }

    private void loadTopData(){
        getNetData(true,true);
    }

    public void scrollAndRefresh(){
        doRecyclerScroll();
        returnTop = true;
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
        }
        page = 0;
    }

    @Override
    public void setVisiable(boolean visiable) {
        if(searchView!=null){
            searchView.setVisibility(visiable?View.VISIBLE:View.INVISIBLE);
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
        if(BmobUser.isLogin()&&header!=null&&!BmobUser.getCurrentUser(User.class).getHeaderUri().isEmpty()){
            String url = BmobUser.getCurrentUser(User.class).getHeaderUri();
            Glide.with(getActivity()).load(url).into(header);
        }else{
            Glide.with(getActivity()).load(R.drawable.header).into(header);
        }
        setVisiable(true);
    }

    private int lovehhyPage = 0;
    private int aidongwuPage = 0;
    private boolean loadStatus = false;
    private boolean loading = false;
    private void getNetData(boolean isLoadLovehhy,boolean top){
        if(loading)
            return;
        loading = true;
        loadStatus = false;
        Random random = new Random();
        int randomNum = random.nextInt(2);
        if(mAdapter.isError())
            mAdapter.setError(false);
        loadLovehhy(top);
    }

    private void loadLovehhy(boolean top){
        LoveNews.updateNews(handler,(++lovehhyPage),top);
    }

    private void loadAidongwu(boolean top){
        Request request = new Request.Builder()
                .get()
                .url("http://www.aidongwu.net/xinwen/page/"+(++aidongwuPage))
                .addHeader("Host","www.aidongwu.net")
                .addHeader("Upgrade-Insecure-Requests","1")
                .addHeader("Referer","http://www.aidongwu.net/xinwen")
                .addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36")
                .addHeader("Connection","keep-alive")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(firstLoading){
                    firstLoading = false;
                }
                refresh.setRefreshing(false);
                loading = false;
                getActivity().runOnUiThread(()-> {
                    Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    if(mAdapter.getItemCount()<=1)
                        mAdapter.setError(true);
                }
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                List<LoveNewsBean> beans = new ArrayList<>();
                Document document = Jsoup.parse(str);
                Elements content_list = document.select("div.content").get(0).select("ul.explist").get(0).select("li");
                for(int i=0;i<content_list.size();i++){
                    LoveNewsBean bean = new LoveNewsBean();
                    Element item = content_list.get(i);
                    bean.setAbsoluteTitle(item.select("a").get(0).attr("title"));
                    bean.setContentUri(item.select("a").get(0).attr("href"));
                    bean.setImg(item.select("a").get(0).select("img").get(0).attr("data-echo"));
                    bean.setContent(item.select("p").get(0).text());
                    bean.setTime(item.select("p.meta").get(0).select("span").get(0).text());
                    bean.setAuthor(item.select("p.meta").get(0).select("span").get(1).select("a").get(0).text());
                    bean.setFlag(1);
                    beans.add(bean);
                }
                if(getActivity()!=null){
                    getActivity().runOnUiThread(()->{
                        if(mAdapter.isError())
                            mAdapter.setError(false);
                        if(!top)
                            mAdapter.addRecomds(beans);
                        else
                            mAdapter.addTopRecomds(beans);
                        refresh.setRefreshing(false);
                        loading = false;
                        if(firstLoading)
                            firstLoading = false;
                        if(mAdapter.isLoading())
                            mAdapter.setLoadingStatus(false);
                    });
                }
            }
        });
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
