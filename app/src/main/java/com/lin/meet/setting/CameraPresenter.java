package com.lin.meet.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class CameraPresenter implements CSettingContract.Presenter {
    private CSettingContract.View view;
    private CSettingContract.Model model;
    public CameraPresenter(CSettingContract.View view){
        this.view=view;
        model=new CameraModel();
    }
    @Override
    public void initSetting(Context context) {
        SharedPreferences pref =model.getSetting(context);
        view.setCheckBox_1(pref.getBoolean("auto_camera",true));
        view.setCheckBox_2(pref.getBoolean("touch_camera",true));
        view.setCheckBox_3(pref.getBoolean("gravity",true));
        view.setCheckBox_4(pref.getBoolean("location",false));
        view.setCheckBox_5(pref.getBoolean("auto_save",true));
    }

    @Override
    public void saveSetting(boolean check_1,boolean check_2,boolean check_3,boolean check_4,boolean check_5,Context context) {
        model.saveSetting(check_1, check_2, check_3, check_4, check_5,context);
    }
}
