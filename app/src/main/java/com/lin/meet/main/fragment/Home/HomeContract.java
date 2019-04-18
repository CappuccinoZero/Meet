package com.lin.meet.main.fragment.Home;

import android.support.v4.app.Fragment;

interface HomeContract {
    interface View{
        void isShowLoad(Boolean s);
        void refreshPictures(PictureFragment fragment);
        void setHeader(String path);
    }
    interface presenter{
        void doRefresh(Fragment fragment, int i);
    }
}
