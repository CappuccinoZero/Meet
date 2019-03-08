package com.lin.meet.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class CameraModel implements CSettingContract.Model {
    @Override
    public SharedPreferences getSetting(Context context) {
        SharedPreferences pref = context.getSharedPreferences("camera_setting",MODE_PRIVATE);
        return pref;
    }

    @Override
    public void saveSetting(boolean check_1, boolean check_2, boolean check_3, boolean check_4, boolean check_5,Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("camera_setting",MODE_PRIVATE).edit();
        editor.putBoolean("auto_camera",check_1);
        editor.putBoolean("touch_camera",check_2);
        editor.putBoolean("gravity",check_3);
        editor.putBoolean("auto_save",check_4);
        editor.putBoolean("location",check_5);
        editor.apply();
    }
}
