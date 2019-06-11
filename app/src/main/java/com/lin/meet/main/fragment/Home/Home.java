package com.lin.meet.main.fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.lin.meet.R;
import com.lin.meet.main.MainActivity;
import com.lin.meet.main.fragment.Know.Know;
import com.lin.meet.override.MyRefresh;
import com.lin.meet.override.MyViewPage;
import com.lin.meet.topic.SendTopic;
import com.lin.meet.video.SendVideo;
import com.xujiaji.happybubble.BubbleDialog;

import cn.jzvd.JZVideoPlayer;


public class Home extends Fragment implements View.OnClickListener,MyViewPage.recyclerStopScroll {
    public static final int END_REFRESH = 100;
    private RequestOptions options;
    private View mView = null;
    private MainActivity activity;
    private MyViewPage viewPager;
    private MyRefresh refresh;
    private BubbleDialog dialog;
    private HomeFragmentAdapter adapter;
    private NavigationTabStrip tabStrip;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case END_REFRESH:
                    refresh.setRefreshing(false);
                    break;
            }
        }
    };


    public void testShow(){
        Toast.makeText(getActivity(),"TestActivity",Toast.LENGTH_SHORT).show();
    }

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
        refresh = (MyRefresh) view.findViewById(R.id.home_refresh);
        refresh.setColorSchemeResources(R.color.teal_A400);
        activity = (MainActivity)getActivity();
        viewPager = (MyViewPage) view.findViewById(R.id.home_viewPage);
        options = new RequestOptions();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        ((TopicFragment) adapter.list.get(0)).refresh(handler);
                        break;
                    case 1:
                        ((Know) adapter.list.get(1)).refresh(handler);
                        break;
                    case 2:
                        ((VideoFragment) adapter.list.get(2)).refresh(handler);
                        break;
                    case 3:
                        PictureFragment fragment = (PictureFragment) adapter.list.get(3);
                        fragment.doRefresh(handler);
                        break;
                }
            }
        });;
        adapter = new HomeFragmentAdapter(this.getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabStrip = (NavigationTabStrip)view.findViewById(R.id.home_tabLayout);
        String title[]=new String[]{"话题","提问","视频","图片"};
        tabStrip.setTitles(title);
        tabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        tabStrip.setViewPager(viewPager);
        viewPager.setRecyclerStop(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.release_1:
                startActivityForResult(new Intent(getActivity(), SendTopic.class),10);
                dialog.dismiss();
                break;
            case R.id.release_2:
                startActivityForResult(new Intent(getActivity(), SendVideo.class),11);
                dialog.dismiss();
                break;
            case R.id.release_3:
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 10 && resultCode == 11){
            ((TopicFragment) adapter.list.get(1)).insertTopic(data.getStringExtra("ID"));
        }
        else if(requestCode == 11 && resultCode == 12){
            ((VideoFragment) adapter.list.get(2)).insertVideo(data.getStringExtra("VIDEO"));
        }
    }

    public void scrollAndRefresh(){
        switch (viewPager.getCurrentItem()){
            case 0:
                ((TopicFragment) adapter.list.get(0)).scrollAndRefresh(handler,()-> refresh.setRefreshing(true));
                break;
            case 1:
                ((Know) adapter.list.get(1)).scrollAndRefresh(handler,()-> refresh.setRefreshing(true));
                break;
            case 2:
                ((VideoFragment) adapter.list.get(2)).scrollAndRefresh(handler,()-> refresh.setRefreshing(true));
                break;
            case 3:
                ((PictureFragment) adapter.list.get(3)).scrollAndRefresh(handler,()-> refresh.setRefreshing(true));
                break;
        }
    }
}
