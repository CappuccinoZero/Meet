package com.lin.meet.main.fragment.Home;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.lin.meet.R;
import com.lin.meet.demo.MyRefresh;
import com.lin.meet.demo.MyViewPage;
import com.lin.meet.main.MainActivity;
import com.lin.meet.my_util.MyUtil;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static cn.jzvd.JZVideoPlayer.TAG;

public class Home extends Fragment implements View.OnClickListener,HomeContract.View,MyViewPage.recyclerStopScroll {
    private View mView = null;
    private HomeContract.presenter presenter;
    private CircleImageView header;
    private Toolbar toolbar;
    private MainActivity activity;
    private MyViewPage viewPager;
    private MyRefresh refresh;
    private LinearLayout loading_view;
    private ImageView release;
    private BubbleDialog dialog;
    private AlertDialog alertDialog;
    View view_0,view_1,view_2,view_3;
    boolean v1=false,v2=false,v3=false;
    private RecyclerView re_recyclerView;
    private RecommendAdapter re_adapter;

    private RecyclerView ps_recyclerView;
    private PicturesAdapter ps_adapter;

    private RecyclerView top_recyclerView;
    private TopicAdapter top_adapter;

    private NavigationTabStrip tabStrip;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view){
        presenter = new HomePresenter(this);
        release = (ImageView)view.findViewById(R.id.home_release);
        refresh = (MyRefresh) view.findViewById(R.id.home_refresh);
        refresh.setColorSchemeResources(R.color.teal_A400);
        toolbar = (Toolbar) view.findViewById(R.id.home_toolbar);
        toolbar.setContentInsetsAbsolute(0,0);
        header = (CircleImageView)view.findViewById(R.id.home_header);
        loading_view = (LinearLayout)view.findViewById(R.id.home_loading);
        activity = (MainActivity)getActivity();
        ((LinearLayout)view.findViewById(R.id.home_header_layout)).setOnClickListener(this);
        viewPager = (MyViewPage) view.findViewById(R.id.home_viewPage);
        view_0 = getLayoutInflater().inflate(R.layout.home_recommend, null);
        view_1 = getLayoutInflater().inflate(R.layout.home_topic, null);
        view_2 = getLayoutInflater().inflate(R.layout.intorduce_view, null);
        view_3 = getLayoutInflater().inflate(R.layout.home_pictures, null);

        release.setOnClickListener(this);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        refresh.setRefreshing(false);
                        break;
                    case 1:
                        refresh.setRefreshing(false);
                        break;
                    case 2:
                        refresh.setRefreshing(false);
                        break;
                    case 3:
                        if(ps_adapter.initLoad){
                            refresh.setRefreshing(false);
                            return;
                        }
                        presenter.doRefresh();
                        break;
                }
            }
        });
        List<View> list=new ArrayList<>();
        list.add(view_0);list.add(view_1);list.add(view_2);list.add(view_3);
        final HomeViwPageAdapter adapter = new HomeViwPageAdapter(list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabStrip = (NavigationTabStrip)view.findViewById(R.id.home_tabLayout);
        String title[]=new String[]{"推荐","话题","视频","美图"};
        tabStrip.setTitles(title);
        tabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        tabStrip.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                isShowLoad(false);
                switch (i){
                    case 0:
                        break;
                    case 1:
                        if(!v1)
                            initTopic();
                        break;
                    case 2:
                        break;
                    case 3:
                        if(!v3)
                            initPictures();
                        if(ps_adapter.initLoad)
                            isShowLoad(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initRecommend();
        ps_adapter = new PicturesAdapter(activity);
        viewPager.setRecyclerStop(this);
    }

    private void showRelaseDialog(){
        BubbleLayout bl = new BubbleLayout(activity);
        bl.setBubbleColor(getResources().getColor(R.color.release_bg));
        bl.setLookLength(50);
        View view = LayoutInflater.from(activity).inflate(R.layout.release,null);
        ((LinearLayout)view.findViewById(R.id.release_1)).setOnClickListener(this);
        ((LinearLayout)view.findViewById(R.id.release_2)).setOnClickListener(this);
        dialog=new BubbleDialog(activity);
        dialog.addContentView(view)
                .setClickedView(release)
                .calBar(true)
                .setBubbleLayout(bl)
                .setTransParentBackground()
                .setPosition(BubbleDialog.Position.BOTTOM)
                .show();
    }

    private void initRecommend(){
        re_recyclerView = (RecyclerView)view_0.findViewById(R.id.re_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
        re_recyclerView.setLayoutManager(manager);
        re_adapter = new RecommendAdapter();
        re_recyclerView.setAdapter(re_adapter);
    }

    private void initPictures(){
        v3 = true;
        ps_recyclerView = (RecyclerView)view_3.findViewById(R.id.pictures_recyclerView);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); 
        ps_recyclerView.setLayoutManager(manager);
        ps_adapter.start(this);
        ps_recyclerView.setAdapter(ps_adapter);
        ps_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){//底部
                    ps_adapter.insertData(activity);
                }
                ps_adapter.roll_dy = dy;
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        ps_adapter.setRolling(false);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        ps_adapter.setRolling(true);
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void initTopic(){
        v1 = true;
        top_recyclerView = (RecyclerView)view_1.findViewById(R.id.topic_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
        top_recyclerView.setLayoutManager(manager);
        top_adapter = new TopicAdapter();
        top_recyclerView.setAdapter(top_adapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_header_layout:
                activity.openDrawer();
                break;
            case R.id.home_release:
                showRelaseDialog();
                break;
            case R.id.release_1:
                //dialog.dismiss();
                break;
            case R.id.release_2:
                //dialog.dismiss();
                break;

        }
    }

    @Override
    public void onResume() {
        if(ps_adapter!=null)
            ps_adapter.clean();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void isShowLoad(final Boolean show) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show)
                    loading_view.setVisibility(View.VISIBLE);
                else
                    loading_view.setVisibility(View.GONE);
            }
        });
    }




    @Override
    public void refreshPictures(){
        try {
            refresh.setRefreshing(false);
            Thread.sleep(150);
            ps_adapter.refresh(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setViewPageItem(int direction) {
        int nowItem = viewPager.getCurrentItem();
        if(direction == MyViewPage.LEFT){
            if(nowItem == 0)
                return;
            viewPager.setCurrentItem(nowItem-1);
        }else if(direction == MyViewPage.RIGHT){
            if(nowItem == 4)
                return;
            viewPager.setCurrentItem(nowItem+1);
        }
    }
}
