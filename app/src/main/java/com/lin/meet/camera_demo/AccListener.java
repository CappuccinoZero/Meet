package com.lin.meet.camera_demo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class AccListener implements SensorEventListener {
    MyCameraContract.AccListener accListener;
    AccListener(MyCameraContract.AccListener accListener){
        this.accListener=accListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: 传感器初始化 A");
        accListener.AccListener(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
