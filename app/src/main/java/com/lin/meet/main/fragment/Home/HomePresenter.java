package com.lin.meet.main.fragment.Home;

import android.support.v4.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

public class HomePresenter implements HomeContract.presenter {
    private HomeContract.View view;
    HomePresenter(HomeContract.View view){
        this.view=view;
    }
    @Override
    public void doRefresh(final Fragment fragment, int i) {
        final Timer t = new Timer();
        switch (i){
            case 3:
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        view.refreshPictures((PictureFragment) fragment);
                        t.cancel();
                    }
                },1500);
                break;
        }
    }
}
