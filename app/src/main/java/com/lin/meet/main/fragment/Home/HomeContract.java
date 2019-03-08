package com.lin.meet.main.fragment.Home;

interface HomeContract {
    interface View{
        void isShowLoad(Boolean s);
        void refreshPictures();
    }
    interface presenter{
        void doRefresh();
    }
}
