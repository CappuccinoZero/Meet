package com.lin.meet.encyclopedia;

import android.widget.ImageView;
import android.widget.TextView;

public interface EncyclopediaContract {
    public interface View{
        void openHorn(ImageView horn);
        void speackText(TextView text, final ImageView horn);
        void setTitleName(String str);
        void setChineseTitle(String str);
        void setEnglishTitle(String str);
        void setContent_0(String str);
        void setContent_1(String str);
        void setContent_2(String str);
        void setContent_3(String str);
        void setContent_4(String str);
        void setContent_5(String str);
        void setImageHead(String path);
        void setImage_1(String path);
        void setImage_2(String path);
    }
}
