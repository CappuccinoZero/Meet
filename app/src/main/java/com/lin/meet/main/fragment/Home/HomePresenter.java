package com.lin.meet.main.fragment.Home;

import java.util.Timer;
import java.util.TimerTask;

public class HomePresenter implements HomeContract.presenter {
    private HomeContract.View view;
    HomePresenter(HomeContract.View view){
        this.view=view;
    }
    @Override
    public void doRefresh() {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                view.refreshPictures();
                t.cancel();
            }
        },1500);
    }
}
