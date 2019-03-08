package com.lin.meet.camera_demo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class GravityListener implements SensorEventListener {
    MyCameraContract.GravityListener gravityListener;
    GravityListener(MyCameraContract.GravityListener gravityListener){
        this.gravityListener = gravityListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gravityListener.GravityListener(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
