package com.lin.meet.main.fragment.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lin.meet.main.fragment.Know.Know;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentAdapter extends FragmentPagerAdapter {
    public List<Fragment> list = new ArrayList<>();
    public HomeFragmentAdapter(FragmentManager fm) {
        super(fm);
        TopicFragment fragment_1 = new TopicFragment();
        Know fragment_2 = new Know();
        VideoFragment fragment_3 = new VideoFragment();
        PictureFragment fragment_4 = new PictureFragment();
        list.add(fragment_1);
        list.add(fragment_2);
        list.add(fragment_3);
        list.add(fragment_4);
    }

    @Override
    public Fragment getItem(int i)
    {
        if(i<list.size())
            return list.get(i);
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
