package com.lin.meet.main.fragment.Home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.lin.meet.R;
import com.lin.meet.main.MainActivity;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.override.MyRefresh;
import com.lin.meet.override.MyViewPage;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.io.File;

import cn.jzvd.JZVideoPlayer;
import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends Fragment implements View.OnClickListener,HomeContract.View,MyViewPage.recyclerStopScroll {
    private RequestOptions options;
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
    private HomeFragmentAdapter adapter;
    View view_0,view_1,view_2,view_3;
    boolean v1=false,v2=false,v3=false;
    private RecyclerView re_recyclerView;
    private RecommendAdapter re_adapter;

    private RecyclerView ps_recyclerView;
    private PicturesAdapter ps_adapter;

    private RecyclerView top_recyclerView;
    private TopicAdapter top_adapter;

    private RecyclerView video_recyclerView;
    private VideoAdapter videoAdapter;

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
        options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        options.skipMemoryCache(true);
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
                        PictureFragment fragment = (PictureFragment) adapter.list.get(3);
                        if(fragment.getinitLoadStatus()){
                            refresh.setRefreshing(false);
                            return;
                        }
                        presenter.doRefresh(fragment,3);
                        break;
                }
            }
        });;
        adapter = new HomeFragmentAdapter(this.getFragmentManager(),this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabStrip = (NavigationTabStrip)view.findViewById(R.id.home_tabLayout);
        String title[]=new String[]{"推荐","话题","视频","美图"};
        tabStrip.setTitles(title);
        tabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        tabStrip.setViewPager(viewPager);
        viewPager.setRecyclerStop(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                isShowLoad(false);
                switch (i){
                    case 3:
                        PictureFragment fragment = (PictureFragment) adapter.list.get(i);
                        if(fragment.getinitLoadStatus())
                            isShowLoad(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
        updateHeader();
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
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
    public void refreshPictures(PictureFragment fragment){
        try {
            refresh.setRefreshing(false);
            Thread.sleep(150);
            fragment.refreshPictures();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setHeader(String path) {
        Glide.with(this).asDrawable().apply(options).load(path).into(header);
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

    public void updateHeader(){
        SharedPreferences pre = MyUtil.getShardPreferences(getActivity(),"Cache");
        String path = pre.getString("header","");
        File file = new File(MainActivity.savePath+path);
        if (file.exists())
            setHeader(MainActivity.savePath+path);
    }
}
