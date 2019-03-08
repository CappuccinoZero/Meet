package com.lin.meet.camera_demo;

import android.hardware.SensorEvent;

public interface MyCameraContract {
    public interface AccListener{
        public void AccListener(SensorEvent sensorEvent);
    }
    public interface GravityListener{
        public void GravityListener(SensorEvent sensorEvent);
    }
}
