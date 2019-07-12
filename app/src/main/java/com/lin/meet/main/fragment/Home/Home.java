package com.lin.meet.main.fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.lin.meet.R;
import com.lin.meet.main.fragment.Know.Know;
import com.lin.meet.override.MyViewPage;


public class Home extends Fragment implements View.OnClickListener,MyViewPage.recyclerStopScroll {
    public static final int END_REFRESH = 100;
    private View mView = null;
    private MyViewPage viewPager;
    private HomeFragmentAdapter adapter;
    private NavigationTabStrip tabStrip;


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
        viewPager = (MyViewPage) view.findViewById(R.id.home_viewPage);
        adapter = new HomeFragmentAdapter(this.getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabStrip = (NavigationTabStrip)view.findViewById(R.id.home_tabLayout);
        String title[]=new String[]{"话题","提问","视频","图片"};
        tabStrip.setTitles(title);
        tabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        tabStrip.setViewPager(viewPager);
        viewPager.setRecyclerStop(this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
    }

    public void scrollAndRefresh(){
        switch (viewPager.getCurrentItem()){
            case 0:
                ((TopicFragment) adapter.list.get(0)).scrollAndRefresh();
                break;
            case 1:
                ((Know) adapter.list.get(1)).scrollAndRefresh();
                break;
            case 2:
                ((VideoFragment) adapter.list.get(2)).scrollAndRefresh();
                break;
            case 3:
                ((PictureFragment) adapter.list.get(3)).scrollAndRefresh();
                break;
        }
    }
}
