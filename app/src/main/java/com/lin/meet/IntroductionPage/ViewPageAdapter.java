package com.lin.meet.IntroductionPage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.demo.SmoothCheckBox;
import com.lin.meet.history.HistoryActivity;
import com.lin.meet.history.HistoryAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private List<View> list;

    public ViewPageAdapter(List<View> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view=list.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(list.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
