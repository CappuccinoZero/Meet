package com.lin.meet.main.fragment.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 实现ViewPage懒加载
 */
public abstract class HomeBaseFragment extends Fragment {
    private boolean isViewCreated;//onCreateView()调用完毕
    private boolean isViewVisible;//View对于用户可见

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isViewVisible = true;
            lazyLoad();
        }else
            isViewVisible = false;
    }

    private void lazyLoad(){
        if(isViewVisible&&isViewCreated){
            loadData();
            isViewCreated = false;
            isViewVisible = false;
        }
    }

    protected abstract void loadData();
}
