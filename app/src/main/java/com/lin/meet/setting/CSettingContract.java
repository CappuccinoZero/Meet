package com.lin.meet.setting;

import android.content.Context;
import android.content.SharedPreferences;

public interface CSettingContract {
    public interface View{
        void setCheckBox_1(boolean check);
        void setCheckBox_2(boolean check);
        void setCheckBox_3(boolean check);
        void setCheckBox_4(boolean check);
        void setCheckBox_5(boolean check);
        void setLayoutVisible(boolean visible);
    }

    public interface Presenter{
        void initSetting(Context context);
        void saveSetting(boolean check_1,boolean check_2,boolean check_3,boolean check_4,boolean check_5,Context context);
    }

    public interface Model{
        SharedPreferences getSetting(Context context);
        void saveSetting(boolean check_1,boolean check_2,boolean check_3,boolean check_4,boolean check_5,Context context);
    }
}
