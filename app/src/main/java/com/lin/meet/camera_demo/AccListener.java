package com.lin.meet.camera_demo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class AccListener implements SensorEventListener {
    MyCameraContract.AccListener accListener;
    AccListener(MyCameraContract.AccListener accListener){
        this.accListener=accListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accListener.AccListener(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
