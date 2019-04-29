package com.lin.meet.encyclopedia;

import android.widget.ImageView;
import android.widget.TextView;

import com.lin.meet.jsoup.BaikeBean;

public interface EncyclopediaContract {
    public interface View{
        void openHorn(ImageView horn);
        void speackText(TextView text, final ImageView horn);
        void setTitleName(String str);
        void setChineseTitle(String str);
        void setEnglishTitle(String str);
        void setImageHead(String path);

        void setSummary(String str);
        void setBaike(int flag,String title,String content,String image);
        void setImage(int flag,String img);
    }
    interface Presenter{
        void initBaike(BaikeBean bean);
    }
}
