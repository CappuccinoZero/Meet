package com.lin.meet.IntroductionPage;

import android.content.Context;
import android.content.SharedPreferences;

import com.lin.meet.camera_demo.PhotoBean;

import static android.content.Context.MODE_PRIVATE;

public class IntroductionModel implements IntorductionContract.Model {

    boolean setting_onSave = false;
    IntroductionModel(Context context){
        SharedPreferences pref = context.getSharedPreferences("camera_setting",MODE_PRIVATE);
        setting_onSave = pref.getBoolean("auto_save",true);
    }

    @Override
    public void updateResult(long time, String newName) {
        String str= time+"";
        PhotoBean bean = new PhotoBean();
        bean.setMaybe(newName);
        bean.updateAll("time = ?",str);
    }

    @Override
    public long savePhoto(String imagePath,String maybe) {
        if(!setting_onSave)
            return 0;
        long save_time = System.currentTimeMillis();
        PhotoBean photo=new PhotoBean();
        photo.setPath(imagePath);
        photo.setTime(save_time);
        photo.setMaybe(maybe);
        photo.setLocation(null);
        photo.save();
        return save_time;
    }
}
